package com.auca.ticketsystem.controller;

import com.auca.ticketsystem.model.*;
import com.auca.ticketsystem.service.SupportAgentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/support-agents")
public class SupportAgentController {

    private final SupportAgentService supportAgentService;

    public SupportAgentController(SupportAgentService supportAgentService) {
        this.supportAgentService = supportAgentService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody SupportAgent supportAgent) {
        String message = supportAgentService.save(supportAgent);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SupportAgent>> getAll() {
        return new ResponseEntity<>(supportAgentService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable UUID id) {
        Optional<SupportAgent> found = supportAgentService.getById(id);
        if (found.isPresent()) {
            return new ResponseEntity<>(found.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("The support agent with that id does not exist", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody SupportAgent supportAgent) {
        String message = supportAgentService.update(id, supportAgent);
        if (message.equals("Support agent updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = supportAgentService.delete(id);
        if (message.equals("Support agent deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    // C1
    @GetMapping("/by-support-area")
    public ResponseEntity<List<SupportAgent>> bySupportArea(@RequestParam String name) {
        return new ResponseEntity<>(supportAgentService.getBySupportArea(name), HttpStatus.OK);
    }

    // D3
    @PutMapping("/{supportAgentId}/support-areas/{supportAreaId}")
    public ResponseEntity<String> addSupportArea(@PathVariable UUID supportAgentId, @PathVariable UUID supportAreaId) {
        String message = supportAgentService.addSupportArea(supportAgentId, supportAreaId);
        if (message.equals("Support area added to the support agent")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals("The support agent already has that support area")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    // D4
    @PatchMapping("/{supportAgentId}/workstation")
    public ResponseEntity<String> changeWorkstation(@PathVariable UUID supportAgentId, @RequestParam int stationNumber) {
        String message = supportAgentService.changeWorkstation(supportAgentId, stationNumber);
        if (message.equals("Workstation changed successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals("That workstation is taken by another support agent")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
