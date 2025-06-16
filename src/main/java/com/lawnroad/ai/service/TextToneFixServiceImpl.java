package com.lawnroad.ai.service;

import com.lawnroad.ai.dto.InterviewChatRequestDto;
import com.lawnroad.ai.dto.InterviewChatResponseDto;
import com.lawnroad.ai.dto.MessageDto;
import com.lawnroad.ai.dto.VariableDto;
import com.lawnroad.common.config.GeminiConfig;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TextToneFixServiceImpl implements TextToneFixService {
  
  @Getter
  private final GeminiConfig geminiConfig;
  private final Client client;
  private final DocumentGenerator documentGenerator;
  
  public TextToneFixServiceImpl(GeminiConfig geminiConfig, DocumentGenerator documentGenerator) {
    this.client = Client.builder()
        .apiKey(geminiConfig.getApiKey())
        .build();
    this.geminiConfig = geminiConfig;
    this.documentGenerator = documentGenerator;
  }
  
  @Override
  public String fixTextTone(String text, String toneKey) {
    String prompt = buildPrompt(text, toneKey);
    GenerateContentResponse response = client.models.generateContent("gemini-2.0-flash", prompt, null);
    return response.text();
  }
  
  private String buildPrompt(String text, String toneKey) {
    return switch (toneKey) {
      case "SPELL" -> """
    You are a professional Korean proofreader.
    Correct any spelling, grammar, or sentence structure errors in the text below.
    Do not change the tone or meaning. Keep line breaks and paragraph formatting exactly the same.
    Return only the corrected Korean text.

    [문장]
    """ + text;
      
      case "PROFESSIONAL" -> """
    You are assisting a Korean lawyer in drafting or refining a legal document template intended for use by clients.

    Rewrite the following text using a strictly professional, clear, and legally precise tone suitable for official documents.

    ❗ Important Rules:
    - The lawyer is writing this document on behalf of a client. Write objectively, not personally.
    - Avoid conversational, speculative, outdated, or ambiguous expressions such as "~같습니다", "~예정입니다", "~사료됩니다".
    - Use definitive legal expressions like "~할 수 없습니다", "~로 간주합니다", "~허용되지 않습니다".
    - Preserve exactly the original number of lines, sentences, and paragraphs.
    - Do not add, remove, or merge any sentences.
    - Return only the rewritten Korean text.

    [문장]
    """ + text;
      
      case "TRUSTWORTHY" -> """
    You are helping a Korean lawyer create a legal document template intended for clients, ensuring clarity, confidence, and trustworthiness.

    Rewrite the following text in a polite but firm, confident, and trustworthy tone appropriate for formal client communication.

    ❗ Important Rules:
    - The lawyer is drafting this for clients, not personal communication.
    - Avoid conversational or uncertain expressions such as "~같습니다", "~일지도 모릅니다", "~예정입니다".
    - Use clear, definitive language (e.g., "~할 수 없습니다", "~로 간주합니다", "~인정되지 않습니다").
    - Keep exactly the same number of sentences, lines, and paragraph breaks as the original text.
    - Do not combine, remove, or add any sentences.
    - Return only the rewritten Korean text.

    [문장]
    """ + text;
      
      case "WARM" -> """
    You are assisting a Korean lawyer to draft legal document templates intended for clients in a warm, empathetic, yet clear and confident manner.

    Rewrite the following text to sound gentle and client-friendly without sacrificing professionalism or clarity.

    ❗ Important Rules:
    - This text is intended for clients, drafted by a lawyer. Do not write emotionally or casually.
    - Avoid overly soft, speculative, or unclear expressions such as "~같습니다", "~해보는 것도 좋습니다", "~예정입니다".
    - Use gentle yet definitive expressions (e.g., "~할 수 없습니다", "~로 간주됩니다", "~이루어지지 않습니다").
    - Maintain exactly the same number of sentences, lines, and paragraphs as the original input.
    - Do not add, remove, or merge sentences.
    - Return only the rewritten Korean text.

    [문장]
    """ + text;
      
      default -> """
    You are assisting a Korean lawyer to improve the fluency, clarity, and suitability of a legal document template intended for client use.

    Rewrite the following text to be clear, natural, and appropriately formal without sounding conversational or overly rigid.

    ❗ Important Rules:
    - The document is written by a lawyer for client use; do not write emotionally or personally.
    - Avoid uncertain, conversational, speculative, or outdated phrases like "~같습니다", "~예정입니다", "~사료됩니다".
    - Employ clear, direct expressions (e.g., "~할 수 없습니다", "~로 간주합니다", "~인정되지 않습니다").
    - Preserve the exact sentence count, line breaks, and paragraph structure of the original text.
    - Do not add, remove, or merge any sentences.
    - Return only the rewritten Korean text.

    [문장]
    """ + text;
    };
  }
  
  
  @Override
  public String generateReply(InterviewChatRequestDto dto) {
    String prompt = buildPrompt(dto);
    GenerateContentResponse response = client.models.generateContent("gemini-2.0-flash", prompt, null);
    return response.text();
  }
  
  @Override
  public InterviewChatResponseDto generateResponse(InterviewChatRequestDto dto) {
    String prompt = buildPrompt(dto);
    GenerateContentResponse response = client.models.generateContent("gemini-2.0-flash", prompt, null);
    String reply = response.text();
    
    boolean allFilled = reply.contains("[문서 생성 완료! 문서를 다운로드 받으세요!]");
    String finalHtml = null;
    
    if (allFilled) {
      // 먼저 AI가 직접 생성한 HTML이 있는지 확인
      String rawHtml = extractFinalDocument(reply);
      
      if (rawHtml != null && !rawHtml.isBlank()) {
        // AI가 작성한 HTML이 있으면 무조건 그걸 사용
        rawHtml = rawHtml.replace("\n", "<br>"); // 줄바꿈 보정
        finalHtml = documentGenerator.wrapAsHtml(rawHtml);
      }
    }
    
    String prettyReply = reply.replace("\n", "<br>");
    return new InterviewChatResponseDto(prettyReply, allFilled, finalHtml);
  }
  private String extractFinalDocument(String aiReply) {
    String signal = "[문서 생성 완료! 문서를 다운로드 받으세요!]";
    int start = aiReply.indexOf(signal);
    
    if (start == -1) return null;
    
    // 신호 뒤부터 끝까지 잘라서 문서로 사용
    String after = aiReply.substring(start + signal.length()).trim();
    
    // 불필요한 코드블럭 표시 제거 (예: ```html ... ```)
    if (after.startsWith("```html")) {
      int htmlStart = after.indexOf("```html") + 7;
      int htmlEnd = after.indexOf("```", htmlStart);
      if (htmlEnd > htmlStart) {
        return after.substring(htmlStart, htmlEnd).trim();
      }
    }
    
    // 그렇지 않다면 그냥 신호 이후 전체 리턴
    return after;
  }
  
  private String extractHtmlPart(String response) {
    int bodyStart = response.indexOf("<body>");
    int bodyEnd = response.indexOf("</body>");
    
    if (bodyStart >= 0 && bodyEnd > bodyStart) {
      return response.substring(bodyStart + 6, bodyEnd).trim(); // <body> 생략한 순수 내용만
    }
    return null;
  }
  
  private String buildPrompt(InterviewChatRequestDto dto) {
    StringBuilder sb = new StringBuilder();
    
    sb.append("""
        너는 한국어 문서 템플릿을 작성하는 변호사를 도와주는 AI 인터뷰 챗봇이야.
        사용자가 문서를 완성할 수 있도록 하나씩 질문하고, 답이 이상하면 다시 물어보거나 보정해야 해.

        아래는 문서 초안이야:
        [본문]
        """).append(dto.getContent()).append("\n\n");
    
    sb.append("""
        아래는 문서 에 대한 설명이야:
        [설명]
        """).append(dto.getDescription()).append("\n\n");
    
    sb.append("[문서에 들어가야 할 항목과 예시값]\n");
    for (VariableDto var : dto.getVariables()) {
      sb.append("- ").append(var.getName())
          .append(": 예시값 → ").append(var.getDescription()).append("\n");
    }
    
    sb.append("[지금까지의 대화]\n");
    for (MessageDto msg : dto.getHistory()) {
      String speaker = msg.getRole().equals("user") ? "사용자" : "AI";
      sb.append(speaker).append(": ").append(msg.getContent().trim())
          .append("\n");
    }
    sb.append("\n위의 대화를 참고해서 다음 질문 또는 문서 생성을 진행하세요.\n");
    
    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
    sb.append(String.format("""

오늘 날짜는 %s입니다. 날짜 관련 질문이 필요한 경우 이 날짜를 기준으로 하세요.

규칙:

사용자 응답 없이 다음 질문으로 넘어가지 마세요. 절대 자문자답하지 마세요.

같은 인삿말이나 이전 응답을 반복하지 말고, 현재 질문에만 집중하세요.

내부 변수명(예: #{이름})은 사용자에게 보여주지 말고 자연스러운 표현으로 바꾸세요.

모든 정보가 수집되면 “[문서 생성 완료! 문서를 다운로드 받으세요!]” 문구와 함께 HTML만 출력하세요. 설명, 마크다운, 추가 멘트는 금지입니다.

추가 지침:

입력이 부정확하거나 애매해도 대략 의미가 명확하면 그대로 받아들이고 진행하세요.

단, 중요한 정보가 빠졌을 경우에만 1회에 한해 간단히 보완 질문을 하세요.
(예: “시간이 빠졌네요. 대략 몇 시쯤인가요?”)

같은 질문을 반복하지 마세요.

이전 입력을 요약하거나 다시 언급하지 말고 바로 다음 항목으로 넘어가세요.

확실하지 않은 답변이라도 문서 작성에 지장이 없다면 진행을 중단하지 말고 이어가세요.

스스로를 변호사나 조력자라고 말하지 마세요.

특수 상황:

문서에 변수가 없다면 질문 없이 즉시 문서를 완성하여 출력하세요.

의미 없는 문자열로만 이루어진 경우라도 변수만 채우면 즉시 문서를 완성하세요.

단, 본문이 숫자 등으로만 채워진 경우 “현재 문서 초안에는 본문 내용이 3으로만 채워져 있습니다. 이 내용을 그대로 출력해도 괜찮으신가요?” 라고 최초 1회만 물어보세요.

문서 구조가 심각히 무너졌거나 작성 불가능한 경우, 즉시 에러를 출력하고 중단하세요.

모든 응답은 자연스럽고 정중한 한국어로 작성하세요.

같은 질문을 반복하지 마세요. 이전 질문에 사용자 응답이 있으면,
그게 '예', '그래', '응', '출력해', '좋아' 등의 긍정적인 표현이라면 다음 단계로 진행하세요.
반복적으로 동일 질문을 하지 마세요. 예외는 없습니다.

문서를 마칠 때는 반드시 [문서 생성 완료! 문서를 다운로드 받으세요!] 를 넣으세요
     """, today));
    
    return sb.toString();
  }
  
}