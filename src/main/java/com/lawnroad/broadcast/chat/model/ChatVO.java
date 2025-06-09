package com.lawnroad.broadcast.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatVO {
    /** PK */
    private Long no;

    /** 사용자 번호 (user_no) */
    private Long userNo;

    /** 방송 번호 (broadcast_no) */
    private Long broadcastNo;

    /** 채팅 보낸 사람 닉네임 */
    private String nickname;

    /** 메시지 내용 */
    private String message;

    /** 신고 여부 (TINYINT) */
    private Integer reportCount;

    /** 생성 일시 */
    private LocalDateTime createdAt;
}
