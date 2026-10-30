package com.auca.ticketsystem.controller;

import com.auca.ticketsystem.model.*;
import com.auca.ticketsystem.service.SupportAreaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/support-areas")
public class SupportAreaController {

    private final SupportAreaService supportAreaService;

    public SupportAreaController(SupportAreaService supportAreaService) {
        this.supportAreaService = supportAreaService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody SupportArea supportArea) {
        String message = supportAreaService.save(supportArea);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SupportArea>> getAll() {
        return new ResponseEntity<>(supportAreaService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable UUID id) {
        Optional<SupportArea> found = supportAreaService.getById(id);
        if (found.isPresent()) {
            return new ResponseEntity<>(found.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("The support area with that id does not exist", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody SupportArea supportArea) {
        String message = supportAreaService.update(id, supportArea);
        if (message.equals("Support area updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = supportAreaService.delete(id);
        if (message.equals("Support area deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    // C3
    @GetMapping("/unused")
    public ResponseEntity<List<SupportArea>> unused() {
        return new ResponseEntity<>(supportAreaService.getUnused(), HttpStatus.OK);
    }
}
