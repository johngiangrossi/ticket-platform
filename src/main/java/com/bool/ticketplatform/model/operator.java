package com.bool.ticketplatform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class operator {


    // fields
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "name is mandatory")
    @Column(nullable=false)
    private String name;

    @NotBlank(message = "email is mandatory")
    @Email(message = "email format is invalid")
    @Column(nullable=false, unique=true)
    private String email;

    @NotBlank(message = "password is mandatory")
    @Size(min=6)
    @Column(nullable=false)
    private String password;

    private boolean available;


    // getters
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }

    public boolean isavAilable() {
        return available;
    }
    
    
    // setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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


    
}
