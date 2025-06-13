package com.lawnroad.ai.service;

import com.lawnroad.common.config.GeminiConfig;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class TextToneFixServiceImpl implements TextToneFixService {
  
  @Getter
  private final GeminiConfig geminiConfig;
  private final Client client;
  
  public TextToneFixServiceImpl(GeminiConfig geminiConfig) {
    this.client = Client.builder()
        .apiKey(geminiConfig.getApiKey())
        .build();
    this.geminiConfig = geminiConfig;
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
  
}