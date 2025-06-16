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
      String rawHtml = extractHtmlPart(reply); // 이 함수는 "<html>" ~ "</html>" 구간 추출
      
      if (rawHtml != null && !rawHtml.isBlank()) {
        // AI가 작성한 HTML이 있으면 무조건 그걸 사용
        finalHtml = documentGenerator.wrapAsHtml(rawHtml);
      }
    }
    
    return new InterviewChatResponseDto(reply, allFilled, finalHtml);
  }
  
  
  private String extractHtmlPart(String response) {
    int start = response.indexOf("<html>");
    int end = response.indexOf("</html>") + "</html>".length();
    
    if (start >= 0 && end > start) {
      return response.substring(start, end);
    }
    return null;
  }
  
//  private String buildFinalHtml(InterviewChatRequestDto dto) {
//    String filled = dto.getContent();
//
//    // 대화 히스토리 순회하면서 AI → 사용자 흐름 분석
//    Map<String, String> variableAnswerMap = new HashMap<>();
//
//    List<MessageDto> history = dto.getHistory();
//    for (int i = 0; i < history.size() - 1; i++) {
//      MessageDto current = history.get(i);
//      MessageDto next = history.get(i + 1);
//
//      // AI가 질문하고, 그 다음 user가 응답한 경우
//      if ("assistant".equals(current.getRole()) && "user".equals(next.getRole())) {
//        // AI 질문에서 변수 이름 추출 시도
//        for (VariableDto var : dto.getVariables()) {
//          if (current.getContent().contains(var.getName()) && !variableAnswerMap.containsKey(var.getName())) {
//            variableAnswerMap.put(var.getName(), next.getContent());
//          }
//        }
//      }
//    }
//
//    // #{변수} 치환
//    for (VariableDto var : dto.getVariables()) {
//      String value = variableAnswerMap.getOrDefault(var.getName(), "___");
//      filled = filled.replace("#{" + var.getName() + "}", value);
//    }
//
//    return filled;
//  }
  
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
    
    sb.append("\n[지금까지의 대화]\n");
    for (MessageDto msg : dto.getHistory()) {
      String speaker = msg.getRole().equals("user") ? "사용자" : "AI";
      sb.append(speaker).append(": ").append(msg.getContent()).append("\n");
    }
    
    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
    sb.append(String.format("""

오늘 날짜는 %s입니다. 날짜 관련 질문이 있을 경우 이 날짜를 기준으로 질문하세요.
규칙:
1. 사용자 응답 없이 다음 질문으로 넘어가지 마세요. 절대 자문자답하지 마세요.
2. 인사말이나 이전 응답을 반복하지 말고, 현재 질문에만 집중하세요.
3. 내부 변수명(예: #{이름})은 사용자에게 보여주지 말고 자연스러운 표현으로 바꾸세요.
4. 모든 정보가 수집되면 “[문서 생성 완료! 문서를 다운로드 받으세요!]” 문구와 함께 HTML만 출력하세요. 설명이나 마크다운은 금지입니다.

모든 응답은 정중하고 자연스러운 한국어로 작성하세요.
     """, today));
    
    return sb.toString();
  }
  
}