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
    /** PK */
    private Long no;

    /** 사용자 번호 (user_no) */
    private Long userNo;

    /** 방송(채팅룸) 번호 (broadcast_no) */
    private Long broadcastNo;

    /** 채팅 보낸 사람 닉네임 */
    private String nickname;

    /** 메시지 내용 */
    private String message;

    /** 생성 일시 */
    private LocalDateTime createdAt;

    /** 채팅 타입 */
    private String type;

}
