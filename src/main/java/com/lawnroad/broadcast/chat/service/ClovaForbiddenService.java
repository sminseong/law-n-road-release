package com.lawnroad.broadcast.chat.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class ClovaForbiddenService {
    @Value("${clova.api-key}")
    private String apiKey;

    @Value("${clova.api-url}")
    private String apiUrl; // 프롬프트: 한 단어(true/false)만, 설명·인사말·기타 불가를 강하게 명시
    private final String systemPrompt = "너는 오직 'true' 또는 'false'로만 답해야 한다. 아래 문장에 욕설, 비속어, 혐오, 폭력적 표현이 있으면 true, 없으면 false만 한 단어로 대답해. 설명, 인사말, 기타 단어를 포함하면 안 된다.";

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
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            InputStream is = code == 200 ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            br.close();

            JSONObject resp = new JSONObject(response.toString());
            // 응답 원본을 반드시 로그로 확인!
            System.out.println("CLOVA 응답 원본: " + resp.toString());

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
                return true; // 보수적으로 차단
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
