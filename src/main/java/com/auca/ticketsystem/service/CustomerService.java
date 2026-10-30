package com.auca.ticketsystem.service;

import com.auca.ticketsystem.model.*;
import com.auca.ticketsystem.repository.*;
import com.auca.ticketsystem.exception.CustomerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final SupportAgentRepository supportAgentRepository;

    public CustomerService(CustomerRepository customerRepository, SupportAgentRepository supportAgentRepository) {
        this.customerRepository = customerRepository;
        this.supportAgentRepository = supportAgentRepository;
    }

    public String save(Customer customer) {
        customer.setId(null);
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer {} created", savedCustomer.getId());
        return "Customer saved successfully";
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getById(UUID id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            logger.info("Customer lookup succeeded; customerId={}", id);
        } else {
            logger.warn("Customer lookup returned no result; customerId={}", id);
        }
        return customer;
    }

    public String update(UUID id, Customer newData) {
        Optional<Customer> found = customerRepository.findById(id);
        if (found.isEmpty()) {
            logger.warn("Customer update rejected; customerId={} status=not_found", id);
            throw new CustomerNotFoundException("The customer with that id does not exist");
        }
        Customer customer = found.get();
        if (newData.getFirstName() != null) {
            customer.setFirstName(newData.getFirstName());
        }
        if (newData.getLastName() != null) {
            customer.setLastName(newData.getLastName());
        }
        if (newData.getAccountNumber() != null) {
            customer.setAccountNumber(newData.getAccountNumber());
        }
        if (newData.getEmail() != null) {
            customer.setEmail(newData.getEmail());
        }
        if (newData.getPhone() != null) {
            customer.setPhone(newData.getPhone());
        }
        customerRepository.save(customer);
        logger.info("Customer update succeeded; customerId={}", id);
        return "Customer updated successfully";
    }

    public String delete(UUID id) {
        if (!customerRepository.existsById(id)) {
            logger.warn("Customer deletion rejected; customerId={} status=not_found", id);
            throw new CustomerNotFoundException("The customer with that id does not exist");
        }
        customerRepository.deleteById(id);
        logger.info("Customer deletion succeeded; customerId={}", id);
        return "Customer deleted successfully";
    }

    // B1
    public List<Customer> getByLastName(String lastName) {
        List<Customer> customers = customerRepository.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
        logger.info("Customer search completed; resultCount={}", customers.size());
        return customers;
    }

    // C4 - empty Optional means the support agent does not exist
    public Optional<List<Customer>> getBySupportAgent(UUID supportAgentId) {
        if (!supportAgentRepository.existsById(supportAgentId)) {
            logger.warn("Customer lookup by support agent returned no result; supportAgentId={} status=not_found",
                    supportAgentId);
            return Optional.empty();
        }
        List<Customer> customers = customerRepository.findBySupportAgentId(supportAgentId);
        logger.info("Customer lookup by support agent completed; supportAgentId={} resultCount={}",
                supportAgentId, customers.size());
        return Optional.of(customers);
    }
}
