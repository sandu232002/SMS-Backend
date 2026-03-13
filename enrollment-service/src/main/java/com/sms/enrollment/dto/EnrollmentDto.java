package com.sms.enrollment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

public class EnrollmentDto {

    @Data
    public static class CreateEnrollmentRequest {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Course ID is required")
        private Long courseId;

        @NotBlank(message = "Academic year is required")
        private String academicYear;

        @NotNull @Min(1)
        private Integer semester;
    }

    @Data
    public static class UpdateStatusRequest {
        @NotBlank(message = "Status is required")
        private String status; // ACTIVE, COMPLETED, DROPPED, SUSPENDED
    }

    @Data
    public static class EnrollmentResponse {
        private Long enrollmentId;
        private Long studentId;
        private String studentName;
        private String studentNumber;
        private Long courseId;
        private String courseName;
        private String courseCode;
        private String academicYear;
        private int semester;
        private LocalDate enrollmentDate;
        private String status;
    }

    @Data
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static <T> ApiResponse<T> success(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }
    }
}
