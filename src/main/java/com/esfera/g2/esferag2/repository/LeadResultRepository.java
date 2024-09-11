package com.esfera.g2.esferag2.repository;

import com.esfera.g2.esferag2.model.LeadResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadResultRepository extends JpaRepository<LeadResult, Long> {
}
