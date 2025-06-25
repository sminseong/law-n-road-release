package com.lawnroad.broadcast.chat.dto;

import lombok.*;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDTO {

    /** PK */
    private Long no;

    /** 사용자 번호 (user_no) */
    private Long userNo;

    private String name;
    /// 방송 번호
    private Long broadcastNo;

    /** 스케줄 번호 */
    private Long scheduleNo;

    /** 채팅 보낸 사람 닉네임 */
    private String nickname;

    /** 메시지 내용 */
    private String message;

    /** 생성 일시 */
    private LocalDateTime createdAt;

    /** 채팅 타입 */
    private String type;
}
