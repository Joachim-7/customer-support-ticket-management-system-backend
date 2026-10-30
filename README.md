# Customer Support Ticket Management System (backend)

- **Student:** MUGISHA Joachim
- **Student ID:** 27256
- **Project:** Customer Support Ticket Management System
- **Course:** INSY 8322 Web Technology and Internet (AUCA), Project Assignment 2026-2027

Spring Boot 3, Spring Data JPA and PostgreSQL REST API. Entities: `Customer`, `SupportAgent`, `SupportArea`, `Workstation`, `Ticket`, `ResolutionNote` (enum `TicketStatus`).

Tickets created without an agent are assigned to the least-loaded active support agent; an agent is considered active when assigned to a workstation. `OPEN` and `IN_PROGRESS` tickets count toward workload, and an agent cannot be assigned a ticket when already handling five open tickets. Explicit agent assignments and ticket reassignments follow the same eligibility and capacity rules.

Ticket-not-found errors return HTTP 404, and agent-capacity errors return HTTP 422 with a JSON `message` describing the failure.

## Steps to run the API

1. Install Java 17+, Maven and PostgreSQL.
2. Create the database: `CREATE DATABASE customer_support_db;`
3. Check the username and password in `src/main/resources/application.properties`.
4. Start the API: `mvn spring-boot:run` (it listens on http://localhost:8080). Hibernate creates the tables (`ddl-auto=update`).
5. Enter the seed data (Step 0) through the save endpoints in Postman, or load `seed.sql` once: `psql -U postgres -d customer_support_db -f seed.sql`.
6. Import `customer-support-ticket-management-system-project.postman_collection.json` into Postman. There is one request per requirement, named by its ID (A1, A2, B1, ..., F2, plus the bonus), and extra "(error)" requests for the failing cases. Collection variables hold the ids of the seed data.

## Endpoints

| Part | Endpoint |
| --- | --- |
| A2 | `POST /api/{path}/save`, `GET /api/{path}/all`, `GET /api/{path}/{id}`, `PUT /api/{path}/update/{id}`, `DELETE /api/{path}/delete/{id}` with `{path}` = support-areas, workstations, support-agents, customers, tickets |
| B1-B3 | `GET /api/customers/by-last-name`, `GET /api/tickets/by-status`, `GET /api/tickets/between` |
| C1-C4 | `GET /api/support-agents/by-support-area`, `GET /api/workstations/free`, `GET /api/support-areas/unused`, `GET /api/customers/of-support-agent/{supportAgentId}` |
| D1-D4 | `POST /api/tickets/save`, `PATCH /api/tickets/{id}/status`, `PUT /api/support-agents/{supportAgentId}/support-areas/{supportAreaId}`, `PATCH /api/support-agents/{supportAgentId}/workstation` |
| E1-E3 | `POST /api/resolution-notes/save?ticketId=`, `GET /api/resolution-notes/customer/{accountNumber}`, `DELETE /api/resolution-notes/delete/{id}` |
| F1-F2 | `GET /api/tickets/stats/by-status`, `PATCH /api/tickets/cancel-day` |
| Bonus | `GET /api/dashboard/summary`, `GET /api/tickets/page` |
