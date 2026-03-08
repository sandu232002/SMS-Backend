package com.sms.student.controller;

import com.sms.student.dto.StudentDto;
import com.sms.student.model.DegreeProgram;
import com.sms.student.repository.DegreeProgramRepository;
import com.sms.student.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final DegreeProgramRepository degreeProgramRepository;

    @PostMapping
    public ResponseEntity<StudentDto.ApiResponse<StudentDto.StudentResponse>> createStudent(
            @Valid @RequestBody StudentDto.CreateStudentRequest request,
            HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("adminId");
        var response = studentService.createStudent(request, adminId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(StudentDto.ApiResponse.success("Student created successfully", response));
    }

    @GetMapping
    public ResponseEntity<StudentDto.ApiResponse<List<StudentDto.StudentResponse>>> getAllStudents(
            @RequestParam(required = false) String search) {
        List<StudentDto.StudentResponse> students = search != null
            ? studentService.searchStudents(search)
            : studentService.getAllStudents();
        return ResponseEntity.ok(StudentDto.ApiResponse.success("Students retrieved", students));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto.ApiResponse<StudentDto.StudentResponse>> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(StudentDto.ApiResponse.success("Student found", studentService.getStudentById(id)));
    }

    @GetMapping("/number/{studentNumber}")
    public ResponseEntity<StudentDto.ApiResponse<StudentDto.StudentResponse>> getStudentByNumber(
            @PathVariable String studentNumber) {
        return ResponseEntity.ok(StudentDto.ApiResponse.success("Student found",
            studentService.getStudentByNumber(studentNumber)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDto.ApiResponse<StudentDto.StudentResponse>> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentDto.UpdateStudentRequest request,
            HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("adminId");
        return ResponseEntity.ok(StudentDto.ApiResponse.success("Student updated",
            studentService.updateStudent(id, request, adminId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StudentDto.ApiResponse<Void>> deleteStudent(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("adminId");
        studentService.deleteStudent(id, adminId);
        return ResponseEntity.ok(StudentDto.ApiResponse.success("Student deleted", null));
    }

    // Degree Program endpoints
    @GetMapping("/degree-programs")
    public ResponseEntity<StudentDto.ApiResponse<List<DegreeProgram>>> getAllDegreePrograms() {
        return ResponseEntity.ok(StudentDto.ApiResponse.success("Degree programs retrieved",
            degreeProgramRepository.findAll()));
    }

    @PostMapping("/degree-programs")
    public ResponseEntity<StudentDto.ApiResponse<DegreeProgram>> createDegreeProgram(
            @Valid @RequestBody StudentDto.DegreeProgramRequest request) {
        DegreeProgram dp = DegreeProgram.builder()
            .degreeName(request.getDegreeName())
            .departmentName(request.getDepartmentName())
            .creditValue(request.getCreditValue())
            .durationYears(request.getDurationYears())
            .build();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(StudentDto.ApiResponse.success("Degree program created",
                degreeProgramRepository.save(dp)));
    }
}
