package com.bool.ticketplatform.model;

import java.util.Collections;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="status_ticket")
public class StatusTicket {


    // fields
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false, unique=true)
    private String statusType;

    
    // fields con relazioni
    @OneToMany(mappedBy = "status")
    private List<Ticket> tickets;



    // getters
    public Integer getId() {
        return id;
    }

    public String getStatusType() {
        return statusType;
    }

    public List<Ticket> getTickets() {
        return Collections.unmodifiableList(tickets);
    }
    

    // setters
    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }


    // metodi
    public void addTicket(Ticket ticket) {
    if (ticket != null && !this.tickets.contains(ticket)) {
        this.tickets.add(ticket);
        ticket.setStatus(this); 
    }
}
    public void removeTicket(Ticket ticket) {
        if (ticket != null) {
            this.tickets.remove(ticket);
            ticket.setStatus(null); 
        }
    }
}
