package com.auca.ticketsystem.controller;

import com.auca.ticketsystem.model.*;
import com.auca.ticketsystem.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Customer customer) {
        String message = customerService.save(customer);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Customer>> getAll() {
        return new ResponseEntity<>(customerService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable UUID id) {
        Optional<Customer> found = customerService.getById(id);
        if (found.isPresent()) {
            return new ResponseEntity<>(found.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("The customer with that id does not exist", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Customer customer) {
        String message = customerService.update(id, customer);
        if (message.equals("Customer updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        String message = customerService.delete(id);
        if (message.equals("Customer deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    // B1
    @GetMapping("/by-last-name")
    public ResponseEntity<List<Customer>> byLastName(@RequestParam String lastName) {
        return new ResponseEntity<>(customerService.getByLastName(lastName), HttpStatus.OK);
    }

    // C4
    @GetMapping("/of-support-agent/{supportAgentId}")
    public ResponseEntity<Object> ofSupportAgent(@PathVariable UUID supportAgentId) {
        Optional<List<Customer>> found = customerService.getBySupportAgent(supportAgentId);
        if (found.isPresent()) {
            return new ResponseEntity<>(found.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("The support agent with that id does not exist", HttpStatus.NOT_FOUND);
    }
}
