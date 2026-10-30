package com.auca.ticketsystem.service;

import com.auca.ticketsystem.model.*;
import com.auca.ticketsystem.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SupportAreaService {

    private final SupportAreaRepository supportAreaRepository;

    public SupportAreaService(SupportAreaRepository supportAreaRepository) {
        this.supportAreaRepository = supportAreaRepository;
    }

    public String save(SupportArea supportArea) {
        supportArea.setId(null);
        supportAreaRepository.save(supportArea);
        return "Support area saved successfully";
    }

    public List<SupportArea> getAll() {
        return supportAreaRepository.findAll();
    }

    public Optional<SupportArea> getById(UUID id) {
        return supportAreaRepository.findById(id);
    }

    public String update(UUID id, SupportArea newData) {
        Optional<SupportArea> found = supportAreaRepository.findById(id);
        if (found.isEmpty()) {
            return "The support area with that id does not exist";
        }
        SupportArea supportArea = found.get();
        if (newData.getName() != null) {
            supportArea.setName(newData.getName());
        }
        supportAreaRepository.save(supportArea);
        return "Support area updated successfully";
    }

    public String delete(UUID id) {
        if (!supportAreaRepository.existsById(id)) {
            return "The support area with that id does not exist";
        }
        supportAreaRepository.deleteById(id);
        return "Support area deleted successfully";
    }

    // C3
    public List<SupportArea> getUnused() {
        return supportAreaRepository.findUnused();
    }
}
