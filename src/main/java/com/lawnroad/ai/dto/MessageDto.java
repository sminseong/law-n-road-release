package com.lawnroad.ai.dto;

import lombok.Data;

@Data
public class MessageDto {
  private String role; // "user" or "assistant"
  private String content;
}