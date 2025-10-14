package com.bool.ticketplatform.model;

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
        return roles;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public List<Note> getNotes() {
        return notes;
    }

    
    // setters
    public void setId(Integer id) {
        this.id = id;
    }

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

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
    
}
