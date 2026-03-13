package com.sms.enrollment.controller;

import com.sms.enrollment.dto.EnrollmentDto;
import com.sms.enrollment.service.EnrollmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentDto.ApiResponse<EnrollmentDto.EnrollmentResponse>> createEnrollment(
            @Valid @RequestBody EnrollmentDto.CreateEnrollmentRequest request,
            HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("adminId");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(EnrollmentDto.ApiResponse.success("Enrollment created",
                enrollmentService.createEnrollment(request, adminId)));
    }

    @GetMapping
    public ResponseEntity<EnrollmentDto.ApiResponse<List<EnrollmentDto.EnrollmentResponse>>> getAllEnrollments(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) Integer semester) {

        List<EnrollmentDto.EnrollmentResponse> enrollments;
        if (studentId != null) {
            enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        } else if (courseId != null) {
            enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        } else if (academicYear != null && semester != null) {
            enrollments = enrollmentService.getEnrollmentsByPeriod(academicYear, semester);
        } else {
            enrollments = enrollmentService.getAllEnrollments();
        }
        return ResponseEntity.ok(EnrollmentDto.ApiResponse.success("Enrollments retrieved", enrollments));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EnrollmentDto.ApiResponse<EnrollmentDto.EnrollmentResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody EnrollmentDto.UpdateStatusRequest request,
            HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("adminId");
        return ResponseEntity.ok(EnrollmentDto.ApiResponse.success("Status updated",
            enrollmentService.updateStatus(id, request, adminId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EnrollmentDto.ApiResponse<Void>> deleteEnrollment(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("adminId");
        enrollmentService.deleteEnrollment(id, adminId);
        return ResponseEntity.ok(EnrollmentDto.ApiResponse.success("Enrollment deleted", null));
    }
}
