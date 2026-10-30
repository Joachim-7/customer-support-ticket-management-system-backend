package com.auca.ticketsystem.repository;

import com.auca.ticketsystem.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    // B2 - DERIVED: enum parameter + OrderBy
    List<Ticket> findByStatusOrderByOpenedDateAsc(TicketStatus status);

    // B3 - DERIVED: Between (both dates inclusive)
    List<Ticket> findByOpenedDateBetweenOrderByOpenedDateAsc(LocalDate start, LocalDate end);

    // D1 - DERIVED: existsBy + And + Not
    boolean existsByAgentAndOpenedDateAndStatusNot(SupportAgent agent, LocalDate openedDate, TicketStatus status);

    long countByAgent_IdAndStatusIn(UUID agentId, Collection<TicketStatus> statuses);

    // F1 - JPQL: GROUP BY + COUNT (a status with zero tickets does not appear)
    @Query("SELECT t.status, COUNT(t) FROM Ticket t GROUP BY t.status")
    List<Object[]> ticketCountsByStatus();

    // F2 - JPQL: one UPDATE query, no Java loop
    @Modifying
    @Query("UPDATE Ticket t SET t.status = :newStatus "
            + "WHERE t.agent.id = :agentId AND t.openedDate = :date AND t.status <> :keepStatus")
    int cancelDay(@Param("agentId") UUID agentId, @Param("date") LocalDate date,
                  @Param("newStatus") TicketStatus newStatus, @Param("keepStatus") TicketStatus keepStatus);

    // Bonus dashboard - DERIVED countBy
    long countByOpenedDate(LocalDate openedDate);

    long countByStatus(TicketStatus status);
}
