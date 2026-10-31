package com.auca.ticketsystem.repository;

import com.auca.ticketsystem.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public interface SupportAgentRepository extends JpaRepository<SupportAgent, UUID> {

    List<SupportAgent> findByWorkstationIsNotNullOrderByIdAsc();

    
    @Query("SELECT DISTINCT a FROM SupportAgent a JOIN a.supportAreas s WHERE LOWER(s.name) = LOWER(:name)")
    List<SupportAgent> findBySupportAreaName(@Param("name") String name);

   
    boolean existsByWorkstation(Workstation workstation);
}
