package com.auca.ticketsystem.service;

import com.auca.ticketsystem.model.*;
import com.auca.ticketsystem.repository.*;
import com.auca.ticketsystem.exception.AgentMaxCapacityException;
import com.auca.ticketsystem.exception.TicketNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    private static final int MAX_OPEN_TICKETS_PER_AGENT = 5;
    private static final List<TicketStatus> OPEN_STATUSES =
            List.of(TicketStatus.OPEN, TicketStatus.IN_PROGRESS);

    private final TicketRepository ticketRepository;
    private final ResolutionNoteRepository resolutionNoteRepository;
    private final SupportAgentRepository supportAgentRepository;

    public TicketService(TicketRepository ticketRepository, ResolutionNoteRepository resolutionNoteRepository,
                         SupportAgentRepository supportAgentRepository) {
        this.ticketRepository = ticketRepository;
        this.resolutionNoteRepository = resolutionNoteRepository;
        this.supportAgentRepository = supportAgentRepository;
    }

    // A2 + D1 (safe saving)
    public String save(Ticket ticket) {
        long startedAt = System.nanoTime();
        logger.info("Starting ticket creation; requestedAgentId={}",
                ticket.getAgent() == null ? null : ticket.getAgent().getId());
        try {
            ticket.setId(null);
            if (ticket.getOpenedDate() == null) {
                ticket.setOpenedDate(LocalDate.now());
            }
            if (ticket.getOpenedDate().isBefore(LocalDate.now())) {
                logger.warn("Ticket creation rejected because opened date {} is in the past",
                        ticket.getOpenedDate());
                return "Ticket date cannot be in the past";
            }
            SupportAgent agent;
            if (ticket.getAgent() == null || ticket.getAgent().getId() == null) {
                logger.debug("No agent specified; selecting least-loaded active agent for date {}",
                        ticket.getOpenedDate());
                agent = findLeastLoadedAgent(ticket.getOpenedDate());
                if (agent == null) {
                    logger.warn("Ticket assignment rejected: no active agent is below the open-ticket limit");
                    throw new AgentMaxCapacityException(
                            "No active support agent is available below the 5-open-ticket limit");
                }
            } else {
                Optional<SupportAgent> foundAgent = supportAgentRepository.findById(ticket.getAgent().getId());
                if (foundAgent.isEmpty()) {
                    logger.warn("Ticket assignment rejected: agent {} was not found", ticket.getAgent().getId());
                    return "The support agent with that id does not exist";
                }
                agent = foundAgent.get();
                if (agent.getWorkstation() == null) {
                    logger.warn("Ticket assignment rejected: agent {} is inactive because they have no workstation",
                            agent.getId());
                    return "Tickets can only be assigned to active support agents";
                }
                long openTickets = countOpenTickets(agent);
                if (openTickets >= MAX_OPEN_TICKETS_PER_AGENT) {
                    logger.warn("Ticket assignment rejected: agent {} has {} open tickets (limit {})",
                            agent.getId(), openTickets, MAX_OPEN_TICKETS_PER_AGENT);
                    throw new AgentMaxCapacityException("Agent already has the maximum of 5 open tickets");
                }
            }
            if (ticketRepository.existsByAgentAndOpenedDateAndStatusNot(
                    agent, ticket.getOpenedDate(), TicketStatus.WITHDRAWN)) {
                logger.warn("Ticket assignment rejected: agent {} already has a ticket on {}",
                        agent.getId(), ticket.getOpenedDate());
                return "Agent already has a ticket on that date";
            }
            ticket.setAgent(agent);
            ticket.setStatus(TicketStatus.OPEN); // a new ticket always starts as OPEN
            ticketRepository.save(ticket);
            logger.info("Ticket created and assigned to agent {}", agent.getId());
            return "Ticket saved successfully";
        } finally {
            logger.info("Ticket creation processing completed in {} ms",
                    elapsedMilliseconds(startedAt));
        }
    }

    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    public Ticket getById(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket " + id + " was not found"));
    }

    // A2 + D2: the general update never touches the status
    public String update(UUID id, Ticket newData) {
        logger.debug("Updating ticket {}", id);
        Optional<Ticket> found = ticketRepository.findById(id);
        if (found.isEmpty()) {
            logger.warn("Ticket update rejected: ticket {} was not found", id);
            throw new TicketNotFoundException("Ticket " + id + " was not found");
        }
        Ticket ticket = found.get();
        SupportAgent updatedAgent = null;
        if (newData.getAgent() != null) {
            long assignmentStartedAt = System.nanoTime();
            logger.debug("Validating reassignment of ticket {}", id);
            try {
                if (newData.getAgent().getId() == null) {
                    logger.warn("Ticket {} reassignment rejected: target agent ID is missing", id);
                    return "A valid support agent must be specified";
                }
                Optional<SupportAgent> foundAgent = supportAgentRepository.findById(newData.getAgent().getId());
                if (foundAgent.isEmpty()) {
                    logger.warn("Ticket {} reassignment rejected: agent {} was not found",
                            id, newData.getAgent().getId());
                    return "The support agent with that id does not exist";
                }
                updatedAgent = foundAgent.get();
                if (updatedAgent.getWorkstation() == null) {
                    logger.warn("Ticket {} reassignment rejected: agent {} is inactive",
                            id, updatedAgent.getId());
                    return "Tickets can only be assigned to active support agents";
                }
                boolean sameAgent = ticket.getAgent() != null
                        && ticket.getAgent().getId().equals(updatedAgent.getId());
                if (!sameAgent) {
                    long openTickets = countOpenTickets(updatedAgent);
                    if (openTickets >= MAX_OPEN_TICKETS_PER_AGENT) {
                        logger.warn("Ticket {} reassignment rejected: agent {} has {} open tickets (limit {})",
                                id, updatedAgent.getId(), openTickets, MAX_OPEN_TICKETS_PER_AGENT);
                        throw new AgentMaxCapacityException("Agent already has the maximum of 5 open tickets");
                    }
                }
            } finally {
                logger.info("Ticket {} reassignment validation completed in {} ms",
                        id, elapsedMilliseconds(assignmentStartedAt));
            }
        }
        if (newData.getOpenedDate() != null) {
            ticket.setOpenedDate(newData.getOpenedDate());
        }
        if (newData.getSubject() != null) {
            ticket.setSubject(newData.getSubject());
        }
        if (newData.getPriority() != null) {
            ticket.setPriority(newData.getPriority());
        }
        if (newData.getCustomer() != null) {
            ticket.setCustomer(newData.getCustomer());
        }
        if (updatedAgent != null) {
            ticket.setAgent(updatedAgent);
        }
        ticketRepository.save(ticket);
        logger.info("Ticket {} updated{}", id,
                updatedAgent == null ? "" : " and assigned to agent " + updatedAgent.getId());
        return "Ticket updated successfully";
    }

    private SupportAgent findLeastLoadedAgent(LocalDate openedDate) {
        long startedAt = System.nanoTime();
        List<SupportAgent> activeAgents = List.of();
        try {
            activeAgents = supportAgentRepository.findByWorkstationIsNotNullOrderByIdAsc();
            logger.debug("Balancing assignment across {} active agents for date {}",
                    activeAgents.size(), openedDate);
            SupportAgent selectedAgent = activeAgents.stream()
                    .filter(agent -> {
                        boolean hasTicketOnDate = ticketRepository.existsByAgentAndOpenedDateAndStatusNot(
                                agent, openedDate, TicketStatus.WITHDRAWN);
                        if (hasTicketOnDate) {
                            logger.debug("Agent {} excluded from date {} assignment: already has a ticket",
                                    agent.getId(), openedDate);
                        }
                        return !hasTicketOnDate;
                    })
                    .map(agent -> new AgentWorkload(agent, countOpenTickets(agent)))
                    .filter(workload -> {
                        boolean underCapacity = workload.openTickets() < MAX_OPEN_TICKETS_PER_AGENT;
                        logger.debug("Assignment candidate agent {} has {} open tickets; eligible={}",
                                workload.agent().getId(), workload.openTickets(), underCapacity);
                        return underCapacity;
                    })
                    .min(Comparator.comparingLong(AgentWorkload::openTickets)
                            .thenComparing(workload -> workload.agent().getId()))
                    .map(AgentWorkload::agent)
                    .orElse(null);
            if (selectedAgent != null) {
                logger.info("Workload balancing selected agent {} for date {}",
                        selectedAgent.getId(), openedDate);
            } else {
                logger.warn("Workload balancing found no eligible agent for date {}", openedDate);
            }
            return selectedAgent;
        } finally {
            logger.info("Workload balancing completed in {} ms across {} active agents",
                    elapsedMilliseconds(startedAt), activeAgents.size());
        }
    }

    private long countOpenTickets(SupportAgent agent) {
        long startedAt = System.nanoTime();
        try {
            long count = ticketRepository.countByAgent_IdAndStatusIn(agent.getId(), OPEN_STATUSES);
            logger.debug("Agent {} workload is {} open tickets", agent.getId(), count);
            return count;
        } finally {
            logger.debug("Agent {} workload count completed in {} ms",
                    agent.getId(), elapsedMilliseconds(startedAt));
        }
    }

    private long elapsedMilliseconds(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }

    private record AgentWorkload(SupportAgent agent, long openTickets) {
    }

    // A2 + E3: a ticket with resolution notes cannot be deleted
    public String delete(UUID id) {
        logger.debug("Deleting ticket {}", id);
        Optional<Ticket> found = ticketRepository.findById(id);
        if (found.isEmpty()) {
            logger.warn("Ticket deletion rejected: ticket {} was not found", id);
            throw new TicketNotFoundException("Ticket " + id + " was not found");
        }
        if (resolutionNoteRepository.existsByTicket(found.get())) {
            logger.warn("Ticket deletion rejected: ticket {} has resolution notes", id);
            return "That ticket has resolution notes, delete them first";
        }
        ticketRepository.deleteById(id);
        logger.info("Ticket {} deleted", id);
        return "Ticket deleted successfully";
    }

    // B2
    public List<Ticket> getByStatus(TicketStatus status) {
        return ticketRepository.findByStatusOrderByOpenedDateAsc(status);
    }

    // B3
    public List<Ticket> getBetween(LocalDate start, LocalDate end) {
        return ticketRepository.findByOpenedDateBetweenOrderByOpenedDateAsc(start, end);
    }

    // D2 (status flow)
    public String changeStatus(UUID id, TicketStatus newStatus) {
        logger.debug("Changing ticket {} status to {}", id, newStatus);
        Optional<Ticket> found = ticketRepository.findById(id);
        if (found.isEmpty()) {
            logger.warn("Ticket status change rejected: ticket {} was not found", id);
            throw new TicketNotFoundException("Ticket " + id + " was not found");
        }
        Ticket ticket = found.get();
        TicketStatus current = ticket.getStatus();
        boolean allowed = (current == TicketStatus.OPEN
                && (newStatus == TicketStatus.IN_PROGRESS || newStatus == TicketStatus.WITHDRAWN))
                || (current == TicketStatus.IN_PROGRESS
                && (newStatus == TicketStatus.RESOLVED || newStatus == TicketStatus.WITHDRAWN));
        if (!allowed) {
            logger.warn("Ticket {} status transition rejected: {} -> {}", id, current, newStatus);
            return "Cannot change status from " + current + " to " + newStatus;
        }
        ticket.setStatus(newStatus);
        ticketRepository.save(ticket);
        logger.info("Ticket {} status changed from {} to {}", id, current, newStatus);
        return "Ticket status updated successfully";
    }

    // F1
    public List<Object[]> countByStatus() {
        return ticketRepository.ticketCountsByStatus();
    }

    // F2: one UPDATE query, the real number comes from the database
    @Transactional
    public String cancelDay(UUID supportAgentId, LocalDate date) {
        logger.info("Cancelling eligible tickets for agent {} on {}", supportAgentId, date);
        long startedAt = System.nanoTime();
        int cancelled = ticketRepository.cancelDay(supportAgentId, date, TicketStatus.WITHDRAWN, TicketStatus.RESOLVED);
        logger.info("Cancelled {} tickets for agent {} on {} in {} ms",
                cancelled, supportAgentId, date, elapsedMilliseconds(startedAt));
        return cancelled + " tickets cancelled";
    }

    // Bonus: paging
    public Page<Ticket> getPage(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }
}
