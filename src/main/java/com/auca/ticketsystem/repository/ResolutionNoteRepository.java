package com.auca.ticketsystem.repository;

import com.auca.ticketsystem.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public interface ResolutionNoteRepository extends JpaRepository<ResolutionNote, UUID> {

    
    boolean existsByTicket(Ticket ticket);

    
    @Query("SELECT n FROM ResolutionNote n WHERE n.ticket.customer.accountNumber = :accountNumber "
            + "ORDER BY n.resolvedDate DESC")
    List<ResolutionNote> findByCustomerAccountNumber(@Param("accountNumber") String accountNumber);
}
