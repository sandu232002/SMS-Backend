package com.sms.course.controller;

import com.sms.course.dto.CourseDto;
import com.sms.course.model.Course;
import com.sms.course.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseDto.ApiResponse<Course>> createCourse(
            @Valid @RequestBody CourseDto.CreateCourseRequest request,
            HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("adminId");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CourseDto.ApiResponse.success("Course created", courseService.createCourse(request, adminId)));
    }

    @GetMapping
    public ResponseEntity<CourseDto.ApiResponse<List<Course>>> getAllCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer semester) {
        List<Course> courses;
        if (search != null) {
            courses = courseService.searchCourses(search);
        } else if (semester != null) {
            courses = courseService.getCoursesBySemester(semester);
        } else {
            courses = courseService.getAllCourses();
        }
        return ResponseEntity.ok(CourseDto.ApiResponse.success("Courses retrieved", courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto.ApiResponse<Course>> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(CourseDto.ApiResponse.success("Course found", courseService.getCourseById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDto.ApiResponse<Course>> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseDto.UpdateCourseRequest request,
            HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("adminId");
        return ResponseEntity.ok(CourseDto.ApiResponse.success("Course updated",
            courseService.updateCourse(id, request, adminId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CourseDto.ApiResponse<Void>> deleteCourse(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("adminId");
        courseService.deleteCourse(id, adminId);
        return ResponseEntity.ok(CourseDto.ApiResponse.success("Course deleted", null));
    }
}
