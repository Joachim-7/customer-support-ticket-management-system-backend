package com.auca.ticketsystem.controller;

import com.auca.ticketsystem.model.*;
import com.auca.ticketsystem.service.WorkstationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/workstations")
public class WorkstationController {

    private final WorkstationService workstationService;

    public WorkstationController(WorkstationService workstationService) {
        this.workstationService = workstationService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Workstation workstation) {
        String message = workstationService.save(workstation);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Workstation>> getAll() {
        return new ResponseEntity<>(workstationService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable UUID id) {
        Optional<Workstation> found = workstationService.getById(id);
        if (found.isPresent()) {
            return new ResponseEntity<>(found.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("The workstation with that id does not exist", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Workstation workstation) {
        String message = workstationService.update(id, workstation);
        if (message.equals("Workstation updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = workstationService.delete(id);
        if (message.equals("Workstation deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    // C2
    @GetMapping("/free")
    public ResponseEntity<List<Workstation>> free() {
        return new ResponseEntity<>(workstationService.getFree(), HttpStatus.OK);
    }
}
