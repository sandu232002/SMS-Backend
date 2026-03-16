package com.sms.enrollment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.enrollment.dto.EnrollmentDto;
import com.sms.enrollment.model.Enrollment;
import com.sms.enrollment.repository.EnrollmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${services.student-url}")
    private String studentServiceUrl;

    @Value("${services.course-url}")
    private String courseServiceUrl;

    @Value("${services.audit-url}")
    private String auditServiceUrl;

    @Transactional
    public EnrollmentDto.EnrollmentResponse createEnrollment(
            EnrollmentDto.CreateEnrollmentRequest request, Long adminId) {

        // Check duplicate
        if (enrollmentRepository.existsByStudentIdAndCourseIdAndAcademicYearAndSemester(
                request.getStudentId(), request.getCourseId(),
                request.getAcademicYear(), request.getSemester())) {
            throw new IllegalArgumentException("Student is already enrolled in this course for the given period");
        }

        // Validate student exists
        validateStudent(request.getStudentId());

        // Validate course exists
        validateCourse(request.getCourseId());

        Enrollment enrollment = Enrollment.builder()
            .studentId(request.getStudentId())
            .courseId(request.getCourseId())
            .academicYear(request.getAcademicYear())
            .semester(request.getSemester())
            .status("ACTIVE")
            .build();

        enrollment = enrollmentRepository.save(enrollment);
        audit("CREATE", "Enrollment", enrollment.getEnrollmentId(),
            "Enrollment created for student " + request.getStudentId() + " in course " + request.getCourseId(),
            adminId);

        return buildResponse(enrollment);
    }

    public List<EnrollmentDto.EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
            .map(this::buildResponse).collect(Collectors.toList());
    }

    public List<EnrollmentDto.EnrollmentResponse> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
            .map(this::buildResponse).collect(Collectors.toList());
    }

    public List<EnrollmentDto.EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
            .map(this::buildResponse).collect(Collectors.toList());
    }

    public List<EnrollmentDto.EnrollmentResponse> getEnrollmentsByPeriod(String academicYear, int semester) {
        return enrollmentRepository.findByAcademicYearAndSemester(academicYear, semester).stream()
            .map(this::buildResponse).collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentDto.EnrollmentResponse updateStatus(
            Long id, EnrollmentDto.UpdateStatusRequest request, Long adminId) {
        Enrollment enrollment = enrollmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));

        enrollment.setStatus(request.getStatus());
        enrollment = enrollmentRepository.save(enrollment);
        audit("UPDATE", "Enrollment", id, "Status changed to " + request.getStatus(), adminId);
        return buildResponse(enrollment);
    }

    @Transactional
    public void deleteEnrollment(Long id, Long adminId) {
        Enrollment e = enrollmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));
        enrollmentRepository.delete(e);
        audit("DELETE", "Enrollment", id, "Enrollment deleted", adminId);
    }

    // --- helpers ---

    private void validateStudent(Long studentId) {
        try {
            restTemplate.getForEntity(studentServiceUrl + "/api/students/" + studentId, Object.class);
        } catch (Exception e) {
            throw new EntityNotFoundException("Student not found with id: " + studentId);
        }
    }

    private void validateCourse(Long courseId) {
        try {
            restTemplate.getForEntity(courseServiceUrl + "/api/courses/" + courseId, Object.class);
        } catch (Exception e) {
            throw new EntityNotFoundException("Course not found with id: " + courseId);
        }
    }

    private EnrollmentDto.EnrollmentResponse buildResponse(Enrollment enrollment) {
        EnrollmentDto.EnrollmentResponse response = new EnrollmentDto.EnrollmentResponse();
        response.setEnrollmentId(enrollment.getEnrollmentId());
        response.setStudentId(enrollment.getStudentId());
        response.setCourseId(enrollment.getCourseId());
        response.setAcademicYear(enrollment.getAcademicYear());
        response.setSemester(enrollment.getSemester());
        response.setEnrollmentDate(enrollment.getEnrollmentDate());
        response.setStatus(enrollment.getStatus());

        // Enrich with student details
        try {
            ResponseEntity<Map> studentResp = restTemplate.getForEntity(
                studentServiceUrl + "/api/students/" + enrollment.getStudentId(), Map.class);
            if (studentResp.getBody() != null) {
                Map<?, ?> data = (Map<?, ?>) studentResp.getBody().get("data");
                if (data != null) {
                    response.setStudentName(data.get("firstName") + " " + data.get("lastName"));
                    response.setStudentNumber(String.valueOf(data.get("studentNumber")));
                }
            }
        } catch (Exception e) {
            log.warn("Could not enrich student info: {}", e.getMessage());
        }

        // Enrich with course details
        try {
            ResponseEntity<Map> courseResp = restTemplate.getForEntity(
                courseServiceUrl + "/api/courses/" + enrollment.getCourseId(), Map.class);
            if (courseResp.getBody() != null) {
                Map<?, ?> data = (Map<?, ?>) courseResp.getBody().get("data");
                if (data != null) {
                    response.setCourseName(String.valueOf(data.get("courseName")));
                    response.setCourseCode(String.valueOf(data.get("courseCode")));
                }
            }
        } catch (Exception e) {
            log.warn("Could not enrich course info: {}", e.getMessage());
        }

        return response;
    }

    private void audit(String actionType, String entityName, Long entityId, String description, Long adminId) {
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
