package com.sms.student.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditClientService {

    private final RestTemplate restTemplate;

    @Value("${services.audit-url}")
    private String auditServiceUrl;

    public void log(String actionType, String entityName, Long entityId, String description, Long adminId) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("actionType", actionType);
            payload.put("entityName", entityName);
            payload.put("entityId", entityId);
            payload.put("description", description);
            payload.put("adminId", adminId);

            restTemplate.postForEntity(auditServiceUrl + "/api/audit/log", payload, Void.class);
        } catch (Exception e) {
            log.warn("Failed to send audit log: {}", e.getMessage());
        }
    }
}
