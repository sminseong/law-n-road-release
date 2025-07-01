package com.lawnroad.payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.payment.service.WebhookService;
import com.lawnroad.payment.verifier.TossWebhookVerifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook/toss")
public class WebhookController {

    private final WebhookService webhookService;
    private final TossWebhookVerifier verifier;
    private final ObjectMapper objectMapper;

    public WebhookController(
            WebhookService webhookService,
            TossWebhookVerifier verifier,
            ObjectMapper objectMapper
    ) {
        this.webhookService = webhookService;
        this.verifier = verifier;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<String> receiveWebhook(
            @RequestHeader("Toss-Signature") String signature,
            @RequestBody String payload
    ) {
        if (!verifier.isValid(payload, signature)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid signature");
        }

        JsonNode json;
        try {
            json = objectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            // 잘못된 JSON → 400 Bad Request
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Malformed JSON payload");
        }

        webhookService.saveWebhook(json);
        return ResponseEntity.ok("OK");
    }
}