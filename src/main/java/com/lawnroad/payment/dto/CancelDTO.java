package com.lawnroad.payment.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CancelDTO {
    private String transactionKey;
    private long cancelAmount;
    private String cancelStatus;
    private String cancelReason;
    private String canceledAt;
    private Map<String, Object> metadata;
}
