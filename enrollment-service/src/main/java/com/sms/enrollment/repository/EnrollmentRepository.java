package com.sms.enrollment.repository;

import com.sms.enrollment.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);
    List<Enrollment> findByAcademicYearAndSemester(String academicYear, int semester);
    Optional<Enrollment> findByStudentIdAndCourseIdAndAcademicYearAndSemester(
        Long studentId, Long courseId, String academicYear, int semester);
    boolean existsByStudentIdAndCourseIdAndAcademicYearAndSemester(
        Long studentId, Long courseId, String academicYear, int semester);
}
