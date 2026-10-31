package com.auca.ticketsystem.controller;

import com.auca.ticketsystem.model.ResolutionNote;
import com.auca.ticketsystem.service.ResolutionNoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resolution-notes")
public class ResolutionNoteController {

    private final ResolutionNoteService resolutionNoteService;

    public ResolutionNoteController(ResolutionNoteService resolutionNoteService) {
        this.resolutionNoteService = resolutionNoteService;
    }

    
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestParam UUID ticketId, @RequestBody ResolutionNote resolutionNote) {
        String message = resolutionNoteService.save(ticketId, resolutionNote);
        if (message.equals("Resolution note saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } else if (message.equals("Resolution notes are only for RESOLVED tickets")) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResolutionNote>> getAll() {
        return new ResponseEntity<>(resolutionNoteService.getAll(), HttpStatus.OK);
    }

    
    @GetMapping("/customer/{accountNumber}")
    public ResponseEntity<List<ResolutionNote>> byCustomer(@PathVariable String accountNumber) {
        return new ResponseEntity<>(resolutionNoteService.getByCustomer(accountNumber), HttpStatus.OK);
    }

   
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = resolutionNoteService.delete(id);
        if (message.equals("Resolution note deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
