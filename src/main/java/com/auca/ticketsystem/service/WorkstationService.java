package com.auca.ticketsystem.service;

import com.auca.ticketsystem.model.*;
import com.auca.ticketsystem.repository.*;
import com.auca.ticketsystem.exception.WorkstationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkstationService {

    private static final Logger logger = LoggerFactory.getLogger(WorkstationService.class);

    private final WorkstationRepository workstationRepository;

    public WorkstationService(WorkstationRepository workstationRepository) {
        this.workstationRepository = workstationRepository;
    }

    public String save(Workstation workstation) {
        workstation.setId(null);
        Workstation savedWorkstation = workstationRepository.save(workstation);
        logger.info("Workstation resource created; workstationId={} stationNumber={}",
                savedWorkstation.getId(), savedWorkstation.getStationNumber());
        return "Workstation saved successfully";
    }

    public List<Workstation> getAll() {
        return workstationRepository.findAll();
    }

    public Optional<Workstation> getById(UUID id) {
        Optional<Workstation> workstation = workstationRepository.findById(id);
        if (workstation.isPresent()) {
            logger.info("Workstation lookup succeeded; workstationId={}", id);
        } else {
            logger.warn("Workstation lookup returned no result; workstationId={} status=not_found", id);
        }
        return workstation;
    }

    public String update(UUID id, Workstation newData) {
        Optional<Workstation> found = workstationRepository.findById(id);
        if (found.isEmpty()) {
            logger.warn("Workstation update rejected; workstationId={} status=not_found", id);
            throw new WorkstationNotFoundException("The workstation with that id does not exist");
        }
        Workstation workstation = found.get();
        if (newData.getName() != null) {
            workstation.setName(newData.getName());
        }
        if (newData.getStationNumber() != 0) {
            workstation.setStationNumber(newData.getStationNumber());
        }
        workstationRepository.save(workstation);
        logger.info("Workstation resource updated; workstationId={} stationNumber={}",
                id, workstation.getStationNumber());
        return "Workstation updated successfully";
    }

    public String delete(UUID id) {
        if (!workstationRepository.existsById(id)) {
            logger.warn("Workstation deletion rejected; workstationId={} status=not_found", id);
            throw new WorkstationNotFoundException("The workstation with that id does not exist");
        }
        workstationRepository.deleteById(id);
        logger.info("Workstation resource deleted; workstationId={}", id);
        return "Workstation deleted successfully";
    }

    // C2
    public List<Workstation> getFree() {
        List<Workstation> freeWorkstations = workstationRepository.findFree();
        logger.info("Free workstation lookup completed; resultCount={}", freeWorkstations.size());
        return freeWorkstations;
    }
}
