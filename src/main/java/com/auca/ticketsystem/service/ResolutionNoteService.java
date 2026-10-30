package com.auca.ticketsystem.service;

import com.auca.ticketsystem.model.*;
import com.auca.ticketsystem.repository.*;
import com.auca.ticketsystem.exception.ResolutionNoteNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResolutionNoteService {

    private static final Logger logger = LoggerFactory.getLogger(ResolutionNoteService.class);

    private final ResolutionNoteRepository resolutionNoteRepository;
    private final TicketRepository ticketRepository;

    public ResolutionNoteService(ResolutionNoteRepository resolutionNoteRepository, TicketRepository ticketRepository) {
        this.resolutionNoteRepository = resolutionNoteRepository;
        this.ticketRepository = ticketRepository;
    }

    // E1
    public String save(UUID ticketId, ResolutionNote resolutionNote) {
        Optional<Ticket> found = ticketRepository.findById(ticketId);
        if (found.isEmpty()) {
            logger.warn("Resolution note creation rejected; ticketId={} status=ticket_not_found", ticketId);
            return "The ticket with that id does not exist";
        }
        if (found.get().getStatus() != TicketStatus.RESOLVED) {
            logger.warn("Resolution note creation rejected; ticketId={} status=ticket_not_resolved",
                    ticketId);
            return "Resolution notes are only for RESOLVED tickets";
        }
        resolutionNote.setId(null);
        resolutionNote.setResolvedDate(LocalDate.now());
        resolutionNote.setTicket(found.get());
        ResolutionNote savedNote = resolutionNoteRepository.save(resolutionNote);
        logger.info("Resolution note created; resolutionNoteId={} ticketId={}",
                savedNote.getId(), ticketId);
        return "Resolution note saved successfully";
    }

    public List<ResolutionNote> getAll() {
        return resolutionNoteRepository.findAll();
    }

    // E2
    public List<ResolutionNote> getByCustomer(String accountNumber) {
        return resolutionNoteRepository.findByCustomerAccountNumber(accountNumber);
    }

    // E3
    public String delete(UUID id) {
        if (!resolutionNoteRepository.existsById(id)) {
            logger.warn("Resolution note deletion rejected; resolutionNoteId={} status=not_found", id);
            throw new ResolutionNoteNotFoundException("The resolution note with that id does not exist");
        }
        resolutionNoteRepository.deleteById(id);
        logger.info("Resolution note removed; resolutionNoteId={}", id);
        return "Resolution note deleted successfully";
    }
}
