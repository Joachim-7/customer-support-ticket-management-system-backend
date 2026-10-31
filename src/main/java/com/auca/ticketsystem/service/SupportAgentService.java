package com.auca.ticketsystem.service;

import com.auca.ticketsystem.model.*;
import com.auca.ticketsystem.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SupportAgentService {

    private static final Logger logger = LoggerFactory.getLogger(SupportAgentService.class);

    private final SupportAgentRepository supportAgentRepository;
    private final SupportAreaRepository supportAreaRepository;
    private final WorkstationRepository workstationRepository;

    public SupportAgentService(SupportAgentRepository supportAgentRepository, SupportAreaRepository supportAreaRepository, WorkstationRepository workstationRepository) {
        this.supportAgentRepository = supportAgentRepository;
        this.supportAreaRepository = supportAreaRepository;
        this.workstationRepository = workstationRepository;
    }

    public String save(SupportAgent supportAgent) {
        supportAgent.setId(null);
        supportAgentRepository.save(supportAgent);
        return "Support agent saved successfully";
    }

    public List<SupportAgent> getAll() {
        return supportAgentRepository.findAll();
    }

    public Optional<SupportAgent> getById(UUID id) {
        return supportAgentRepository.findById(id);
    }

    public String update(UUID id, SupportAgent newData) {
        Optional<SupportAgent> found = supportAgentRepository.findById(id);
        if (found.isEmpty()) {
            return "The support agent with that id does not exist";
        }
        SupportAgent supportAgent = found.get();
        if (newData.getFirstName() != null) {
            supportAgent.setFirstName(newData.getFirstName());
        }
        if (newData.getLastName() != null) {
            supportAgent.setLastName(newData.getLastName());
        }
        if (newData.getEmail() != null) {
            supportAgent.setEmail(newData.getEmail());
        }
        if (newData.getWorkstation() != null) {
            supportAgent.setWorkstation(newData.getWorkstation());
        }
        if (newData.getSupportAreas() != null) {
            supportAgent.setSupportAreas(newData.getSupportAreas());
        }
        supportAgentRepository.save(supportAgent);
        return "Support agent updated successfully";
    }

    public String delete(UUID id) {
        if (!supportAgentRepository.existsById(id)) {
            return "The support agent with that id does not exist";
        }
        supportAgentRepository.deleteById(id);
        return "Support agent deleted successfully";
    }


    public List<SupportAgent> getBySupportArea(String name) {
        return supportAgentRepository.findBySupportAreaName(name);
    }

    
    public String addSupportArea(UUID supportAgentId, UUID supportAreaId) {
        Optional<SupportAgent> foundAgent = supportAgentRepository.findById(supportAgentId);
        if (foundAgent.isEmpty()) {
            return "The support agent with that id does not exist";
        }
        Optional<SupportArea> foundArea = supportAreaRepository.findById(supportAreaId);
        if (foundArea.isEmpty()) {
            return "That support area does not exist";
        }
        SupportAgent supportAgent = foundAgent.get();
        SupportArea supportArea = foundArea.get();
        for (SupportArea held : supportAgent.getSupportAreas()) {
            if (held.getId().equals(supportArea.getId())) {
                return "The support agent already has that support area";
            }
        }
        supportAgent.getSupportAreas().add(supportArea);
        supportAgentRepository.save(supportAgent);
        return "Support area added to the support agent";
    }

   
    public String changeWorkstation(UUID supportAgentId, int stationNumber) {
        Optional<SupportAgent> foundAgent = supportAgentRepository.findById(supportAgentId);
        if (foundAgent.isEmpty()) {
            logger.warn("Workstation allocation rejected; supportAgentId={} status=agent_not_found",
                    supportAgentId);
            return "The support agent with that id does not exist";
        }
        Optional<Workstation> foundWorkstation = workstationRepository.findByStationNumber(stationNumber);
        if (foundWorkstation.isEmpty()) {
            logger.warn("Workstation allocation rejected; stationNumber={} status=workstation_not_found",
                    stationNumber);
            return "The workstation with that stationNumber does not exist";
        }
        SupportAgent supportAgent = foundAgent.get();
        Workstation workstation = foundWorkstation.get();
        boolean alreadyHis = supportAgent.getWorkstation() != null
                && supportAgent.getWorkstation().getId().equals(workstation.getId());
        if (!alreadyHis && supportAgentRepository.existsByWorkstation(workstation)) {
            logger.warn("Workstation allocation rejected; supportAgentId={} stationNumber={} status=occupied",
                    supportAgentId, stationNumber);
            return "That workstation is taken by another support agent";
        }
        UUID previousWorkstationId = supportAgent.getWorkstation() == null
                ? null : supportAgent.getWorkstation().getId();
        supportAgent.setWorkstation(workstation);
        supportAgentRepository.save(supportAgent);
        logger.info("Workstation allocation updated; supportAgentId={} previousWorkstationId={} "
                        + "workstationId={} stationNumber={}",
                supportAgentId, previousWorkstationId, workstation.getId(), stationNumber);
        return "Workstation changed successfully";
    }
}
