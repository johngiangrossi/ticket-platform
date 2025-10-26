package com.bool.ticketplatform.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class User {


    // fields
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "username is mandatory")
    @Column(nullable=false, unique=true)
    private String username;

    @NotBlank(message = "email is mandatory")
    @Email(message = "email format is invalid")
    @Column(nullable=false, unique=true)
    private String email;

    @NotBlank(message = "password is mandatory")
    @Size(min=6)
    @Column(nullable=false)
    private String password;

    private boolean available;


    // fields con relazioni
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @OneToMany(mappedBy = "user")
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "user")
    private List<Note> notes;

    

    // getters
    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }

    public boolean isAvailable() {
        return available;
    }
    
    public List<Role> getRoles() {
        return Collections.unmodifiableList(roles);
    }

    public List<Ticket> getTickets() {
        return Collections.unmodifiableList(tickets);
    }

    public List<Note> getNotes() {
        return Collections.unmodifiableList(notes);
    }

    
    // setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // metodi
    public void addRole(Role role) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }

        if (role != null && !this.roles.contains(role)) {
            this.roles.add(role);
        }
    }
    
    public void addTicket(Ticket ticket) {
        if (ticket != null && !this.tickets.contains(ticket)) {
            this.tickets.add(ticket);
            ticket.setUser(this); 
        }
    }
    
    public void addNote(Note note) {
        if (note != null && !this.notes.contains(note)) {
            this.notes.add(note);
            note.setUser(this);
        }
    }
    
    public void removeTicket(Ticket ticket) {
        if (ticket != null && !this.tickets.contains(ticket)) {
            this.tickets.remove(ticket);
            ticket.setUser(this); 
        }
    }
    
    public void removeNote(Note note) {
        if (note != null && !this.notes.contains(note)) {
            this.notes.remove(note);
            note.setUser(this);
        }
    }   
}
