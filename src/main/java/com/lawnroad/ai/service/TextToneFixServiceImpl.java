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
    
    boolean allFilled = reply.contains("[문서 생성 완료]");
    String finalHtml = null;
    
    if (allFilled) {
      if (reply.contains("<html>")) {
        String rawHtml = extractHtmlPart(reply);
        finalHtml = documentGenerator.wrapAsHtml(rawHtml);  // PDF 변환 가능
      } else {
        // GPT가 HTML을 안 줬으면 우리가 buildFinalHtml로 생성
        String filledHtml = buildFinalHtml(dto);
        finalHtml = documentGenerator.wrapAsHtml(filledHtml);
      }
    }
    
    return new InterviewChatResponseDto(reply, allFilled, finalHtml);
  }
  
  
  
  private String extractHtmlPart(String text) {
    int start = text.indexOf("<html>");
    int end = text.indexOf("</html>") + "</html>".length();
    return (start >= 0 && end > start) ? text.substring(start, end) : text;
  }
  
  private String buildFinalHtml(InterviewChatRequestDto dto) {
    String filled = dto.getContent();
    
    // 대화 히스토리 순회하면서 AI → 사용자 흐름 분석
    Map<String, String> variableAnswerMap = new HashMap<>();
    
    List<MessageDto> history = dto.getHistory();
    for (int i = 0; i < history.size() - 1; i++) {
      MessageDto current = history.get(i);
      MessageDto next = history.get(i + 1);
      
      // AI가 질문하고, 그 다음 user가 응답한 경우
      if ("assistant".equals(current.getRole()) && "user".equals(next.getRole())) {
        // AI 질문에서 변수 이름 추출 시도
        for (VariableDto var : dto.getVariables()) {
          if (current.getContent().contains(var.getName()) && !variableAnswerMap.containsKey(var.getName())) {
            variableAnswerMap.put(var.getName(), next.getContent());
          }
        }
      }
    }
    
    // #{변수} 치환
    for (VariableDto var : dto.getVariables()) {
      String value = variableAnswerMap.getOrDefault(var.getName(), "___");
      filled = filled.replace("#{" + var.getName() + "}", value);
    }
    
    return filled;
  }
  
  private String buildPrompt(InterviewChatRequestDto dto) {
    StringBuilder sb = new StringBuilder();
    
    sb.append("""
        너는 한국어 문서 템플릿을 작성하는 변호사를 도와주는 AI 인터뷰 챗봇이야.
        사용자가 문서를 완성할 수 있도록 하나씩 질문하고, 답이 이상하면 다시 물어보거나 보정해야 해.

        아래는 문서 초안이야:
        [본문]
        """).append(dto.getContent()).append("\n\n");
    
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
    
    sb.append("""
        
        You are an AI chatbot that helps collect necessary information for drafting Korean legal documents through a structured interview.
        
        Use the list of variables and their example values below to determine what information is needed and continue the conversation naturally, one question at a time.
        
        Instructions:
        - If the user input is vague, incorrect, or incomplete, briefly explain and ask again.
        - If the answer is sufficient, move to the next item. Do not repeat or summarize previous answers.
        - Do NOT recap previous answers. Each response should only include the next necessary question.
        - Ignore any emotional or aggressive language and respond neutrally without reacting.
        - Never interpret or comment on the user's emotional state (e.g., “You seem upset” is not allowed).
        - Never use narrative cues or stage directions like "(pause)", "(*sigh*)", or anything in parentheses.
        - Do not ask or answer your own questions — never do self-dialogue.
        - At the beginning, clearly confirm the user's identity and their role (e.g., victim or counterparty). This role must remain fixed.
        - Do not move to the next question without an explicit user response. Never assume.
        - Never expose internal variable names such as `name`, `incidentDate`, etc. Use natural and culturally appropriate Korean language instead.
        - Once all required fields are collected, output the phrase exactly: \s
          `[문서 생성 완료]`
          - Each response must only contain the next single question. \s
            Do NOT repeat or summarize previous answers or questions. \s
            For example, if the user already answered with their name, do not restate it. \s
            Just ask the next question directly.
        - Immediately after that, generate the complete final document in clean HTML format only. Do not include extra comments or explanations. The HTML must be fully self-contained and production-ready.
        
          \s
        """);
    
    return sb.toString();
  }
  
}