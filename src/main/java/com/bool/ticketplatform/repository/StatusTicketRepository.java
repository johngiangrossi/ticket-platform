package com.bool.ticketplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bool.ticketplatform.model.StatusTicket;

public interface StatusTicketRepository extends JpaRepository<StatusTicket, Integer> {

}
