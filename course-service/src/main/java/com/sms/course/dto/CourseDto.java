package com.sms.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class CourseDto {

    @Data
    public static class CreateCourseRequest {
        @NotBlank(message = "Course code is required")
        private String courseCode;

        @NotBlank(message = "Course name is required")
        private String courseName;

        @NotNull @Min(1)
        private Integer creditValue;

        @NotNull @Min(1)
        private Integer semester;
    }

    @Data
    public static class UpdateCourseRequest {
        private String courseName;
        private Integer creditValue;
        private Integer semester;
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
