package com.auca.ticketsystem.service;

import com.auca.ticketsystem.model.TicketStatus;
import com.auca.ticketsystem.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final SupportAgentRepository supportAgentRepository;
    private final WorkstationRepository workstationRepository;
    private final TicketRepository ticketRepository;
    private final ResolutionNoteRepository resolutionNoteRepository;

    public DashboardService(CustomerRepository customerRepository, SupportAgentRepository supportAgentRepository,
                            WorkstationRepository workstationRepository, TicketRepository ticketRepository,
                            ResolutionNoteRepository resolutionNoteRepository) {
        this.customerRepository = customerRepository;
        this.supportAgentRepository = supportAgentRepository;
        this.workstationRepository = workstationRepository;
        this.ticketRepository = ticketRepository;
        this.resolutionNoteRepository = resolutionNoteRepository;
    }

    public Map<String, Long> getSummary() {
        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("customers", customerRepository.count());
        summary.put("agents", supportAgentRepository.count());
        summary.put("freeWorkstations", (long) workstationRepository.findFree().size()); // reuses C2
        summary.put("ticketsToday", ticketRepository.countByOpenedDate(LocalDate.now()));
        summary.put("open", ticketRepository.countByStatus(TicketStatus.OPEN));
        summary.put("resolutionNotes", resolutionNoteRepository.count());
        return summary;
    }
}
