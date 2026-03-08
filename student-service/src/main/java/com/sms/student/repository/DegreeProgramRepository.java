package com.sms.student.repository;

import com.sms.student.model.DegreeProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DegreeProgramRepository extends JpaRepository<DegreeProgram, Long> {
    boolean existsByDegreeName(String degreeName);
}
