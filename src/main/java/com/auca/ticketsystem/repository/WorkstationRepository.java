package com.auca.ticketsystem.repository;

import com.auca.ticketsystem.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public interface WorkstationRepository extends JpaRepository<Workstation, UUID> {

    // C2 - JPQL: workstations that no support agent occupies
    @Query("SELECT w FROM Workstation w WHERE NOT EXISTS "
            + "(SELECT a FROM SupportAgent a WHERE a.workstation = w) ORDER BY w.stationNumber")
    List<Workstation> findFree();

    // D4 - DERIVED
    Optional<Workstation> findByStationNumber(int stationNumber);
}
