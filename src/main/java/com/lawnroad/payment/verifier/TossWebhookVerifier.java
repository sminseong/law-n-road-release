package com.lawnroad.payment.verifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TossWebhookVerifier {
    @Value("${tosspayments.secret-key}")
    private String secretKey;

    public boolean isValid(String payload, String signature) {
        String computed =
                org.apache.commons.codec.digest.HmacUtils
                        .hmacSha256Hex(secretKey, payload);
        return computed.equals(signature);
    }
}
