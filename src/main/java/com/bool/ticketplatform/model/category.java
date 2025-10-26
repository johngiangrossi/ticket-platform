package com.bool.ticketplatform.model;

import java.util.Collections;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Category {

    // fields
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "name is mandatory")
    @Column(nullable=false, unique=true)
    private String name;


    // fields con relazioni
    @OneToMany(mappedBy = "category")
    private List<Ticket> tickets;

    

    // getters
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Ticket> getTickets() {
        return Collections.unmodifiableList(tickets);
    }
    

    // setters
    public void setName(String name) {
        this.name = name;
    }
    

    // metodi
    public void addTicket(Ticket ticket) {
        if (ticket != null && !this.tickets.contains(ticket)) {
            this.tickets.add(ticket);
            ticket.setCategory(this);
        }
    }
    public void removeTicket(Ticket ticket) {
        if (ticket != null) {
            this.tickets.remove(ticket);
            ticket.setCategory(null); 
        }
    }
}
