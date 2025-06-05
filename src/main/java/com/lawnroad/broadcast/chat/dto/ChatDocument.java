package com.lawnroad.broadcast.chat.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDocument {

    @Id
    private String id;

    private Long no;
    private Long userNo;
    private Long broadcastNo;
    private String nickname;
    private String message;
    private Integer reportCount;
    private LocalDateTime createdAt;
}
