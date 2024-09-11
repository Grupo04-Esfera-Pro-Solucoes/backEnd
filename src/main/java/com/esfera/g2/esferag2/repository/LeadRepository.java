package com.esfera.g2.esferag2.repository;

import com.esfera.g2.esferag2.model.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    Boolean existsByIdClientIdClient(Long idClient);
    
    Page<Lead> findAll(Pageable pageable);

    Long countByDateBetweenAndIdClientUserIdUser(Timestamp start, Timestamp end, Long idUser);

    Page<Lead> findLeadsByIdClientNameContainingIgnoreCaseAndIdClientUserIdUser(String name, Long idUser, Pageable pageable);

    Page<Lead> findAllByIdClientUserIdUser(Long idUser, Pageable pageable);

    Lead findByIdLeadAndIdClientUserIdUser(Long id, Long idUser);

    List<Lead> findLeadsByIdClientUserIdUser(Long idUser);

    @Query("SELECT COUNT(l.idLead) FROM Lead l JOIN l.idClient c JOIN c.user u WHERE l.date >= :startDate AND l.date < :endDate AND u.idUser = :idUser")
    Double sumValue(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate, @Param("idUser") Long idUser);

    default Double sumValue(Long idUser) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate startOfNextMonth = startOfMonth.plusMonths(1);
        return sumValue(Timestamp.valueOf(startOfMonth.atStartOfDay()), Timestamp.valueOf(startOfNextMonth.atStartOfDay()), idUser);
    }

    default Double sumValueLastMonth(Long idUser) {
        LocalDate now = LocalDate.now();
        LocalDate startOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDate startOfThisMonth = now.withDayOfMonth(1);
        return sumValue(Timestamp.valueOf(startOfLastMonth.atStartOfDay()), Timestamp.valueOf(startOfThisMonth.atStartOfDay()), idUser);
    }

    @Query("SELECT r.result, COUNT(l) FROM Lead l JOIN l.result r JOIN l.idClient c JOIN c.user u WHERE u.idUser = :idUser AND (CASE WHEN :period = 'day' THEN DATE(l.date) = CURRENT_DATE WHEN :period = 'week' THEN YEARWEEK(l.date, 1) = YEARWEEK(CURRENT_DATE, 1) WHEN :period = 'month' THEN YEAR(l.date) = YEAR(CURRENT_DATE) AND MONTH(l.date) = MONTH(CURRENT_DATE) ELSE TRUE END) GROUP BY r.result")
    List<Object[]> countLeadsByResult(@Param("idUser") Long idUser, @Param("period") String period);

    @Query("SELECT l FROM Lead l JOIN l.idClient c JOIN c.user u WHERE u.idUser = :idUser AND DATE(l.date) = :date")
    Page<Lead> findAllByIdClientUserIdUserAndDate(
            @Param("idUser") Long idUser, 
            @Param("date") LocalDate date, 
            Pageable pageable);
}
