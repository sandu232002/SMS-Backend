package com.sms.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentDto {

    @Data
    public static class CreateStudentRequest {
        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        private String address;
        private LocalDate dateOfBirth;

        @NotNull(message = "Degree program ID is required")
        private Long degreeProgramId;
    }

    @Data
    public static class UpdateStudentRequest {
        private String firstName;
        private String lastName;
        private String address;
        private LocalDate dateOfBirth;
        private Long degreeProgramId;
    }

    @Data
    public static class StudentResponse {
        private Long studentId;
        private String studentNumber;
        private String firstName;
        private String lastName;
        private String address;
        private LocalDate dateOfBirth;
        private LocalDateTime createdAt;
        private Long degreeProgramId;
        private String degreeProgramName;

        public StudentResponse(com.sms.student.model.Student s, String degreeProgramName) {
            this.studentId = s.getStudentId();
            this.studentNumber = s.getStudentNumber();
            this.firstName = s.getFirstName();
            this.lastName = s.getLastName();
            this.address = s.getAddress();
            this.dateOfBirth = s.getDateOfBirth();
            this.createdAt = s.getCreatedAt();
            this.degreeProgramId = s.getDegreeProgramId();
            this.degreeProgramName = degreeProgramName;
        }
    }

    @Data
    public static class DegreeProgramRequest {
        @NotBlank(message = "Degree name is required")
        private String degreeName;

        @NotBlank(message = "Department name is required")
        private String departmentName;

        @NotNull(message = "Credit value is required")
        private Integer creditValue;

        @NotNull(message = "Duration years is required")
        private Integer durationYears;
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
