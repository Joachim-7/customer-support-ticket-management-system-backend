package com.auca.ticketsystem.repository;

import com.auca.ticketsystem.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    
    List<Ticket> findByStatusOrderByOpenedDateAsc(TicketStatus status);

    
    List<Ticket> findByOpenedDateBetweenOrderByOpenedDateAsc(LocalDate start, LocalDate end);

    
    boolean existsByAgentAndOpenedDateAndStatusNot(SupportAgent agent, LocalDate openedDate, TicketStatus status);

    long countByAgent_IdAndStatusIn(UUID agentId, Collection<TicketStatus> statuses);

    
    @Query("SELECT t.status, COUNT(t) FROM Ticket t GROUP BY t.status")
    List<Object[]> ticketCountsByStatus();

    
    @Modifying
    @Query("UPDATE Ticket t SET t.status = :newStatus "
            + "WHERE t.agent.id = :agentId AND t.openedDate = :date AND t.status <> :keepStatus")
    int cancelDay(@Param("agentId") UUID agentId, @Param("date") LocalDate date,
                  @Param("newStatus") TicketStatus newStatus, @Param("keepStatus") TicketStatus keepStatus);

    
    long countByOpenedDate(LocalDate openedDate);

    long countByStatus(TicketStatus status);
}
