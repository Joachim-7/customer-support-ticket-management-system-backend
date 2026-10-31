package com.auca.ticketsystem.repository;

import com.auca.ticketsystem.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public interface SupportAreaRepository extends JpaRepository<SupportArea, UUID> {

    @Query("SELECT a FROM SupportArea a WHERE a.agents IS EMPTY")
    List<SupportArea> findUnused();
}
