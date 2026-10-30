package com.auca.ticketsystem.repository;

import com.auca.ticketsystem.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    // B1 - DERIVED: IgnoreCase + OrderBy
    List<Customer> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

    // C4 - JPQL: DISTINCT + path navigation
    @Query("SELECT DISTINCT t.customer FROM Ticket t WHERE t.agent.id = :supportAgentId")
    List<Customer> findBySupportAgentId(@Param("supportAgentId") UUID supportAgentId);
}
