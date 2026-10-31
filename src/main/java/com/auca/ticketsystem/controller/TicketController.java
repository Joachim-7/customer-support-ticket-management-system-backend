package com.auca.ticketsystem.controller;

import com.auca.ticketsystem.model.*;
import com.auca.ticketsystem.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Ticket ticket) {
        String message = ticketService.save(ticket);
        if (message.equals("Ticket saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } else if (message.equals("Ticket date cannot be in the past")) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Ticket>> getAll() {
        return new ResponseEntity<>(ticketService.getAll(), HttpStatus.OK);
    }

   
    @GetMapping("/by-status")
    public ResponseEntity<List<Ticket>> byStatus(@RequestParam TicketStatus status) {
        return new ResponseEntity<>(ticketService.getByStatus(status), HttpStatus.OK);
    }

    
    @GetMapping("/between")
    public ResponseEntity<List<Ticket>> between(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return new ResponseEntity<>(ticketService.getBetween(startDate, endDate), HttpStatus.OK);
    }

    
    @GetMapping("/stats/by-status")
    public ResponseEntity<List<Object[]>> statsByStatus() {
        return new ResponseEntity<>(ticketService.countByStatus(), HttpStatus.OK);
    }

    
    @GetMapping("/page")
    public ResponseEntity<Page<Ticket>> page(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size,
                                             @RequestParam(defaultValue = "openedDate,desc") String sort) {
        String[] parts = sort.split(",");
        Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, parts[0]));
        return new ResponseEntity<>(ticketService.getPage(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable UUID id) {
        return new ResponseEntity<>(ticketService.getById(id), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Ticket ticket) {
        String message = ticketService.update(id, ticket);
        if (message.equals("The support agent with that id does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(message, message.equals("Ticket updated successfully")
                ? HttpStatus.OK : HttpStatus.CONFLICT);
    }

   
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = ticketService.delete(id);
        if (message.equals("Ticket deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> changeStatus(@PathVariable UUID id, @RequestParam TicketStatus status) {
        String message = ticketService.changeStatus(id, status);
        if (message.equals("Ticket status updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.startsWith("Cannot change status")) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    
    @PatchMapping("/cancel-day")
    public ResponseEntity<String> cancelDay(@RequestParam UUID supportAgentId, @RequestParam String date) {
        LocalDate day = LocalDate.parse(date);
        return new ResponseEntity<>(ticketService.cancelDay(supportAgentId, day), HttpStatus.OK);
    }
}
