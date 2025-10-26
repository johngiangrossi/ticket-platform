package com.bool.ticketplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bool.ticketplatform.model.StatusTicket;
import com.bool.ticketplatform.model.Ticket;
import com.bool.ticketplatform.model.User;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    // cerco per titolo
    public List<Ticket> findByTitleContainingIgnoreCase(String title);

    // cerco per nome utente
    public List<Ticket> findByUser_Username(String username);

    // cerco per nome utente e titolo
    public List<Ticket> findByUser_UsernameAndTitleContainingIgnoreCase(String username, String title);
    
    // cerco per utente
    public List<Ticket> findByUser(User user);

    // cerco per categoria nome
    public List<Ticket> findByCategory_nameContainingIgnoreCase(String name);

    // cerco per stato tipo
    public List<Ticket> findByStatus_statusTypeContainingIgnoreCase(String statusType);

    // cerco per categoria nome e stato tipo
    public List<Ticket> findByCategory_nameAndStatus_statusTypeContainingIgnoreCase(String name, String statusType);

    // cerco per nome utente e categoria nome
    public List<Ticket> findByUser_UsernameAndCategory_nameContainingIgnoreCase(String username, String name);

    // cerco per nome utente e stato tipo
    public List<Ticket> findByUser_UsernameAndStatus_statusTypeContainingIgnoreCase(String username, String statusType);

}
