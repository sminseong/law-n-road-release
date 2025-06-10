package com.lawnroad.broadcast.chat.model;

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
public class ChatVO {

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
