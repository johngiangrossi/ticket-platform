package com.bool.ticketplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bool.ticketplatform.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
