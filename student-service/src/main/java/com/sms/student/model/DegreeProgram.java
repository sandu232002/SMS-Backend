package com.sms.student.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "degree_programs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DegreeProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "degree_program_id")
    private Long degreeProgramId;

    @Column(name = "degree_name", nullable = false)
    private String degreeName;

    @Column(name = "department_name", nullable = false)
    private String departmentName;

    @Column(name = "credit_value", nullable = false)
    private int creditValue;

    @Column(name = "duration_years", nullable = false)
    private int durationYears;
}
