package com.sms.student.service;

import com.sms.student.dto.StudentDto;
import com.sms.student.model.DegreeProgram;
import com.sms.student.model.Student;
import com.sms.student.repository.DegreeProgramRepository;
import com.sms.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final DegreeProgramRepository degreeProgramRepository;
    private final AuditClientService auditClientService;

    @Transactional
    public StudentDto.StudentResponse createStudent(StudentDto.CreateStudentRequest request, Long adminId) {
        String studentNumber = generateStudentNumber();

        if (studentRepository.existsByStudentNumber(studentNumber)) {
            throw new IllegalArgumentException("Generated student number already exists: " + studentNumber);
        }

        DegreeProgram dp = degreeProgramRepository.findById(request.getDegreeProgramId())
            .orElseThrow(() -> new EntityNotFoundException("Degree program not found"));

        Student student = Student.builder()
            .studentNumber(studentNumber)
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .address(request.getAddress())
            .dateOfBirth(request.getDateOfBirth())
            .degreeProgramId(request.getDegreeProgramId())
            .build();

        student = studentRepository.save(student);
        auditClientService.log("CREATE", "Student", student.getStudentId(),
            "Student created: " + student.getStudentNumber(), adminId);

        return new StudentDto.StudentResponse(student, dp.getDegreeName());
    }

    public List<StudentDto.StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
            .map(s -> {
                String dpName = degreeProgramRepository.findById(s.getDegreeProgramId())
                    .map(DegreeProgram::getDegreeName).orElse("Unknown");
                return new StudentDto.StudentResponse(s, dpName);
            })
            .collect(Collectors.toList());
    }

    public StudentDto.StudentResponse getStudentById(Long id) {
        Student s = studentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        String dpName = degreeProgramRepository.findById(s.getDegreeProgramId())
            .map(DegreeProgram::getDegreeName).orElse("Unknown");
        return new StudentDto.StudentResponse(s, dpName);
    }

    public StudentDto.StudentResponse getStudentByNumber(String number) {
        Student s = studentRepository.findByStudentNumber(number)
            .orElseThrow(() -> new EntityNotFoundException("Student not found: " + number));
        String dpName = degreeProgramRepository.findById(s.getDegreeProgramId())
            .map(DegreeProgram::getDegreeName).orElse("Unknown");
        return new StudentDto.StudentResponse(s, dpName);
    }

    public List<StudentDto.StudentResponse> searchStudents(String name) {
        return studentRepository.searchByName(name).stream()
            .map(s -> {
                String dpName = degreeProgramRepository.findById(s.getDegreeProgramId())
                    .map(DegreeProgram::getDegreeName).orElse("Unknown");
                return new StudentDto.StudentResponse(s, dpName);
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public StudentDto.StudentResponse updateStudent(Long id, StudentDto.UpdateStudentRequest request, Long adminId) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

        if (request.getFirstName() != null) student.setFirstName(request.getFirstName());
        if (request.getLastName() != null) student.setLastName(request.getLastName());
        if (request.getAddress() != null) student.setAddress(request.getAddress());
        if (request.getDateOfBirth() != null) student.setDateOfBirth(request.getDateOfBirth());
        if (request.getDegreeProgramId() != null) {
            degreeProgramRepository.findById(request.getDegreeProgramId())
                .orElseThrow(() -> new EntityNotFoundException("Degree program not found"));
            student.setDegreeProgramId(request.getDegreeProgramId());
        }

        student = studentRepository.save(student);
        auditClientService.log("UPDATE", "Student", student.getStudentId(),
            "Student updated: " + student.getStudentNumber(), adminId);

        String dpName = degreeProgramRepository.findById(student.getDegreeProgramId())
            .map(DegreeProgram::getDegreeName).orElse("Unknown");
        return new StudentDto.StudentResponse(student, dpName);
    }

    @Transactional
    public void deleteStudent(Long id, Long adminId) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        studentRepository.delete(student);
        auditClientService.log("DELETE", "Student", id,
            "Student deleted: " + student.getStudentNumber(), adminId);
    }

    private String generateStudentNumber() {
        int nextSequence = studentRepository.findTopByOrderByStudentIdDesc()
            .map(Student::getStudentId)
            .map(Long::intValue)
            .map(id -> id + 1)
            .orElse(1);

        String year = String.valueOf(LocalDate.now().getYear());
        return String.format("STU%s-%04d", year, nextSequence);
    }
}
