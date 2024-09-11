package com.esfera.g2.esferag2.repository;

import com.esfera.g2.esferag2.model.Proposal;
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
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    Boolean existsByIdLeadIdLead(Long leadId);

    long countByProposalDateBetweenAndIdLeadIdClientUserIdUser(Timestamp start, Timestamp end, Long userId);

    Page<Proposal> findByIdLeadIdClientUserIdUser(Long idUser, Pageable pageable);

    List<Proposal> findByIdLeadIdClientUserIdUser(Long idUser);

    Page<Proposal> findByIdLeadIdClientNameContainingIgnoreCaseAndIdLeadIdClientUserIdUser(String name, Long idUser, Pageable pageable);

    Proposal findByIdProposalAndIdLeadIdClientUserIdUser(Long id, Long idUser);


    @Query("SELECT SUM(p.value) FROM Proposal p JOIN p.idLead l JOIN l.idClient c JOIN p.idStatusProposal sp JOIN c.user u WHERE p.proposalDate >= :startDate AND p.proposalDate < :endDate AND sp.name = 'Fechado' AND u.idUser = :idUser")
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

    @Query("SELECT COUNT(p.idProposal) FROM Proposal p JOIN p.idLead l JOIN l.idClient c JOIN c.user u WHERE p.proposalDate >= :startDate AND p.proposalDate < :endDate AND u.idUser = :idUser")
    Double sumValue2(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate, @Param("idUser") Long idUser);

    default Double sumValue2(Long idUser) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate startOfNextMonth = startOfMonth.plusMonths(1);
        return sumValue2(Timestamp.valueOf(startOfMonth.atStartOfDay()), Timestamp.valueOf(startOfNextMonth.atStartOfDay()), idUser);
    }

    default Double sumValueLastMonth2(Long idUser) {
        LocalDate now = LocalDate.now();
        LocalDate startOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDate startOfThisMonth = now.withDayOfMonth(1);
        return sumValue2(Timestamp.valueOf(startOfLastMonth.atStartOfDay()), Timestamp.valueOf(startOfThisMonth.atStartOfDay()), idUser);
    }

    @Query("SELECT sp.name, COUNT(p) FROM Proposal p JOIN p.idLead l JOIN l.idClient c JOIN p.idStatusProposal sp JOIN c.user u WHERE u.idUser = :idUser AND (CASE WHEN :period = 'day' THEN DATE(p.proposalDate) = CURRENT_DATE WHEN :period = 'week' THEN YEARWEEK(p.proposalDate, 1) = YEARWEEK(CURRENT_DATE, 1) WHEN :period = 'month' THEN YEAR(p.proposalDate) = YEAR(CURRENT_DATE) AND MONTH(p.proposalDate) = MONTH(CURRENT_DATE) ELSE TRUE END) GROUP BY sp.name")
    List<Object[]> countProposalsByStatus(@Param("idUser") Long idUser, @Param("period") String period);
}
