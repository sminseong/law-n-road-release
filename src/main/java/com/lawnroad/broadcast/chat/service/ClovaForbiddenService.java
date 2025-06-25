package com.lawnroad.broadcast.chat.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class ClovaForbiddenService {
//    @Value("${clova.api-key}")
    private String apiKey;

    //151번째 줄만 수정할것
    @Value("${clova.api-url}")
    private String apiUrl;
    private final String systemPrompt = """
너는 사람들 간의 대화에서 사용된 표현을 분석하여, 문장에 공격적, 모욕적, 성적 불쾌감, 혐오, 비하, 폭력, 위협 등의 부적절한 내용이 실제로 포함됐는지 판단하는 AI이다.

반드시 문장 전체의 맥락, 의도, 감정, 대상에 대한 태도를 우선하여 판단해야 한다.
절대로 단어나 단어 조합만으로 판단하지 말고, 반드시 문장 전체의 목적과 의도만을 기준으로 분석하라.

---

📌 [응답 형식]

- 반드시 다음 두 가지 중 하나로 응답하라:
  - true: 공격적, 혐오적, 성적 불쾌감, 모욕적, 위협적 표현이 명확히 포함된 문장 (금칙어)
  - false: 일상적이고 중립적이며, 공격 의도가 없는 문장 (칭찬, 감탄, 농담, 설명 포함, 일반 채팅)

- 절대 다른 단어나 설명을 추가하지 말고, 반드시 true 또는 false 단 하나의 단어만 반환하라.

---

📌 [true로 판단해야 하는 경우]

오직 다음 조건 중 하나 이상을 명백히 만족하는 경우에만 true로 판단하라:

- 직접적인 욕설이나 비속어를 사용하여 상대를 공격, 모욕, 비하하는 경우
  (예: 씨X, 꺼져 XX야, 병X, 개XX 등 명백히 모욕적 표현이 명시된 경우)

- 성적 모욕, 성희롱이 명확한 경우
  (예: 노골적 신체 언급, 성적 대상화가 뚜렷하고 불쾌함을 유발할 목적일 때)

- 폭력적이거나 협박하는 표현이 명확히 포함된 경우
  (예: 죽여버릴 거야, 패버릴 거야 등 직접적인 폭력 표현)

- 혐오나 차별 표현으로 명확히 특정 집단을 비하하거나 조롱하는 경우
  (예: 특정 인종, 성별, 나이, 지역, 외모 등을 직접적으로 공격할 때)

---

📌 [반드시 false로 판단해야 하는 경우 (중요!)]

다음 중 하나라도 해당하면 무조건 false로 판단하라.
절대로 단어 조합만 보고 오판하지 마라:

- 욕설처럼 보이는 단어가 있지만, 전체 문장이 공격 목적이 없을 경우
  예시: "수박씨 발라먹어", "아저씨 발 예뻐요", "발 냄새 나요", "씨를 뿌리다", "개발자", "시발점" 등은 반드시 false로 판단하라.

- 일상적인 감탄, 칭찬, 농담, 설명, 평가로서 공격 의도가 없는 경우
  예시: "몸매 좋으시네요", "진짜 멋있어요", "미치겠네", "짜증나네" (단순 감정 표현)

- 문장이 애매하거나 판단이 조금이라도 모호한 경우
  → GPT는 무조건 false로 판단해야 하며, 절대로 보수적으로 true로 판단하지 마라.

- 단어가 욕설과 유사해도 문장 전체의 의미가 중립적/긍정적인 경우
  → 무조건 false로 처리하라.

---

📌 [AI 판단 핵심 원칙]

- 절대 단어나 단어 조합만으로 판단하지 마라.
- 반드시 문장 전체의 목적, 의도, 감정, 대상에 대한 태도만을 기준으로 판단하라.
- 조금이라도 공격성, 성적 모욕, 위협의 의도가 명확하지 않으면 반드시 false로 판단하라.
- 과잉 차단(false positive)을 반드시 피하라.

---

📌 [응답 예시 (실제 응답에 포함하지 않음)]

- "씨X 진짜 짜증나" → true
- "수박씨 발라먹어" → false
- "아저씨 발 예뻐요" → false
- "발 냄새 나요" → false
- "시발점은 여기야" → false
- "좆같은 상황이다" (개인 감정) → false
- "이 XX야 꺼져" → true

---

📌 [최종 규칙]

- 응답은 반드시 'true' 또는 'false' 중 단 하나의 단어만 출력하라.
- 다른 단어, 문장, 부연, 설명, 기호, 따옴표 등을 절대 추가하지 말 것.
- 판단이 애매하거나 조금이라도 모호하면 반드시 false로 응답하라.
""";





    public boolean containsProhibitedWords(String userMsg) {
        try {
            JSONArray messages = new JSONArray()
                    .put(new JSONObject().put("role", "system").put("content", systemPrompt))
                    .put(new JSONObject().put("role", "user").put("content", userMsg));

            JSONObject requestBody = new JSONObject()
                    .put("messages", messages)
                    .put("maxTokens", 5) // 불필요한 길이 방지
                    .put("temperature", 0.0); // 일관성↑

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            InputStream is = code == 200 ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            br.close();

            JSONObject resp = new JSONObject(response.toString());
            String answer = "";
            if (resp.has("result") && resp.getJSONObject("result").has("message")) {
                answer = resp.getJSONObject("result")
                        .getJSONObject("message")
                        .getString("content")
                        .replaceAll("[^a-zA-Z]", "")
                        .trim()
                        .toLowerCase();
            } else {
                System.err.println("CLOVA 알 수 없는 응답: " + resp.toString());
                return false;
            }

            System.out.println("AI 응답: [" + answer + "]");

            if (answer.equals("true")) {
                return true;   // 금칙어/욕설 포함 (차단)
            } else if (answer.equals("false")) {
                return false;  // 정상
            } else {
                // 인사말/설명/불명확 → 모두 차단
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true; // 예외 상황은 모두 차단
        }
    }
}
