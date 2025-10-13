package com.bool.ticketplatform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class statusTicket {


    // fields
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message="status is mandatory")
    @Column(nullable=false, unique=true)
    private String status;


    // getters
    public Integer getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
    

    // setters
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

}
