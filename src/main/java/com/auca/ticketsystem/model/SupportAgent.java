package com.auca.ticketsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "support_agent")
public class SupportAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    @ManyToOne
    @JoinColumn(name = "workstation_id")
    private Workstation workstation;

    @ManyToMany
    @JoinTable(
            name = "support_agent_support_area",
            joinColumns = @JoinColumn(name = "support_agent_id"),
            inverseJoinColumns = @JoinColumn(name = "support_area_id"))
    private List<SupportArea> supportAreas = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Workstation getWorkstation() {
        return workstation;
    }

    public void setWorkstation(Workstation workstation) {
        this.workstation = workstation;
    }

    public List<SupportArea> getSupportAreas() {
        return supportAreas;
    }

    public void setSupportAreas(List<SupportArea> supportAreas) {
        this.supportAreas = supportAreas;
    }
}
