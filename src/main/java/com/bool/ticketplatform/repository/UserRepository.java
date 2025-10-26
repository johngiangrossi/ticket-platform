package com.bool.ticketplatform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bool.ticketplatform.model.User;



public interface UserRepository extends JpaRepository<User, Integer> {

    // cerco per nome
    public Optional<User> findByUsername(String username);

    // cerco per disponibilità
    public List<User> findByAvailable(boolean available);

    // cerco per email
    public Optional<User> findByEmail(String email);
}

