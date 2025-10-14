package com.bool.ticketplatform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bool.ticketplatform.model.User;


public interface UserRepository extends JpaRepository<User, Integer> {

    public Optional<User> findByUsername(String username);
}

