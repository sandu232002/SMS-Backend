package com.sms.audit.controller;

import com.sms.audit.model.AuditLog;
import com.sms.audit.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    // Internal endpoint - called by other services
    @PostMapping("/log")
    public ResponseEntity<AuditLog> recordLog(
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return ResponseEntity.ok(auditService.recordLog(payload, ip));
    }

    @GetMapping
    public ResponseEntity<Page<AuditLog>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getAll(page, size));
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<AuditLog>> getByAdmin(@PathVariable Long adminId) {
        return ResponseEntity.ok(auditService.getByAdmin(adminId));
    }

    @GetMapping("/entity/{entityName}/{entityId}")
    public ResponseEntity<List<AuditLog>> getByEntity(
            @PathVariable String entityName, @PathVariable Long entityId) {
        return ResponseEntity.ok(auditService.getByEntity(entityName, entityId));
    }

    @GetMapping("/action/{actionType}")
    public ResponseEntity<List<AuditLog>> getByActionType(@PathVariable String actionType) {
        return ResponseEntity.ok(auditService.getByActionType(actionType));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Audit service is running");
    }
}
