package com.lawnroad.payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.payment.service.WebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private final WebhookService webhookService;
    private final ObjectMapper objectMapper;

    public WebhookController(WebhookService webhookService, ObjectMapper objectMapper) {
        this.webhookService = webhookService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<?> receiveWebhook(@RequestBody JsonNode payload) {
        webhookService.saveWebhook(payload);
        return ResponseEntity.ok().body("웹훅 수신 및 저장 완료");
    }
}
