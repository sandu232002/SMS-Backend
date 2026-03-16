package com.sms.course.service;

import com.sms.course.dto.CourseDto;
import com.sms.course.model.Course;
import com.sms.course.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final RestTemplate restTemplate;

    @Value("${services.audit-url}")
    private String auditServiceUrl;

    @Transactional
    public Course createCourse(CourseDto.CreateCourseRequest request, Long adminId) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new IllegalArgumentException("Course code already exists: " + request.getCourseCode());
        }

        Course course = Course.builder()
            .courseCode(request.getCourseCode())
            .courseName(request.getCourseName())
            .creditValue(request.getCreditValue())
            .semester(request.getSemester())
            .build();

        course = courseRepository.save(course);
        audit("CREATE", "Course", course.getCourseId(), "Course created: " + course.getCourseCode(), adminId);
        return course;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
    }

    public List<Course> searchCourses(String query) {
        return courseRepository.searchCourses(query);
    }

    public List<Course> getCoursesBySemester(int semester) {
        return courseRepository.findBySemester(semester);
    }

    @Transactional
    public Course updateCourse(Long id, CourseDto.UpdateCourseRequest request, Long adminId) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));

        if (request.getCourseName() != null) course.setCourseName(request.getCourseName());
        if (request.getCreditValue() != null) course.setCreditValue(request.getCreditValue());
        if (request.getSemester() != null) course.setSemester(request.getSemester());

        course = courseRepository.save(course);
        audit("UPDATE", "Course", course.getCourseId(), "Course updated: " + course.getCourseCode(), adminId);
        return course;
    }

    @Transactional
    public void deleteCourse(Long id, Long adminId) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
        courseRepository.delete(course);
        audit("DELETE", "Course", id, "Course deleted: " + course.getCourseCode(), adminId);
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
