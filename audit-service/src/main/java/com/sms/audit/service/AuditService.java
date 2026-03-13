package com.sms.audit.service;

import com.sms.audit.model.AuditLog;
import com.sms.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditLog recordLog(Map<String, Object> payload, String ipAddress) {
        AuditLog log = AuditLog.builder()
            .actionType(String.valueOf(payload.get("actionType")))
            .entityName(String.valueOf(payload.get("entityName")))
            .entityId(payload.get("entityId") != null
                ? Long.parseLong(String.valueOf(payload.get("entityId"))) : null)
            .description(String.valueOf(payload.get("description")))
            .ipAddress(ipAddress)
            .adminId(payload.get("adminId") != null
                ? Long.parseLong(String.valueOf(payload.get("adminId"))) : null)
            .build();
        return auditLogRepository.save(log);
    }

    public Page<AuditLog> getAll(int page, int size) {
        return auditLogRepository.findAllByOrderByTimestampDesc(
            PageRequest.of(page, size, Sort.by("timestamp").descending()));
    }

    public List<AuditLog> getByAdmin(Long adminId) {
        return auditLogRepository.findByAdminId(adminId);
    }

    public List<AuditLog> getByEntity(String entityName, Long entityId) {
        return auditLogRepository.findByEntityNameAndEntityId(entityName, entityId);
    }

    public List<AuditLog> getByActionType(String actionType) {
        return auditLogRepository.findByActionType(actionType);
    }
}
