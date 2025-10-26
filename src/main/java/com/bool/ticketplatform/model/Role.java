package com.bool.ticketplatform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Role {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "role name is mandatory")
    @Column(nullable=false, unique=true)
    private String roleName;



    // getters
    public Integer getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }


    // setters
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
}
