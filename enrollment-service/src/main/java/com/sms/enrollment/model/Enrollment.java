package com.sms.enrollment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "enrollments",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id", "academic_year", "semester"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "semester", nullable = false)
    private int semester;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @PrePersist
    protected void onCreate() {
        if (enrollmentDate == null) enrollmentDate = LocalDate.now();
        if (status == null) status = "ACTIVE";
    }

    public void getStudent() { /* fetched via student-service */ }
    public void getCourse()  { /* fetched via course-service  */ }
}
