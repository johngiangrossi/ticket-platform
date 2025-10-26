package com.bool.ticketplatform.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Ticket {

    // fields
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "title is mandatory")
    @Column(nullable=false)
    private String title;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateCreation;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateUpdate;

    
    // fields con relazioni
    @OneToMany(mappedBy="ticket")
    private List<Note> notes;
    
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "status_ticket_id", nullable = false)
    private StatusTicket status;
    
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "category is mandatory")
    private Category category;

  
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "user is mandatory")
    private User user;
    


    // getters
    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDateUpdate() {
        return dateUpdate;
    }

    public StatusTicket getStatus() {
        return status;
    }

    public List<Note> getNotes() {
        return Collections.unmodifiableList(notes);
    }
    
    public Category getCategory() {
        return category;
    }

    public User getUser() {
        return user;
    }


    // setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDateUpdate(LocalDateTime dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStatus(StatusTicket status) {
        this.status = status;
    }


    // metodi
    public void addNote(Note note) {
        if (note != null && !this.notes.contains(note)) {
            this.notes.add(note);
            note.setTicket(this); 
        }
    }

    // Metodo per rimuovere una nota
    public void removeNote(Note note) {
        if (note != null) {
            this.notes.remove(note);
            note.setTicket(null); 
        }
    }
}
