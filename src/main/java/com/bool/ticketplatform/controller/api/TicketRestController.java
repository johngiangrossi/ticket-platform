package com.bool.ticketplatform.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bool.ticketplatform.model.Ticket;
import com.bool.ticketplatform.repository.TicketRepository;


@RestController
@CrossOrigin
@RequestMapping("/api/tickets")
public class TicketRestController {

    @Autowired
    private TicketRepository ticketRepository;

    // get per filtrare
    @GetMapping
    public List<Ticket> getMethodName(@RequestParam(name = "categoryName", required = false) String categoryName, @RequestParam(name = "statusName", required = false) String statusName) {
        List<Ticket> result = null;

        if (categoryName != null && !categoryName.isBlank() && statusName != null && !statusName.isBlank()) {
            return ticketRepository.findByCategory_nameAndStatus_statusTypeContainingIgnoreCase(categoryName, statusName);
        } else if (categoryName != null && !categoryName.isBlank()) {
            result = ticketRepository.findByCategory_nameContainingIgnoreCase(categoryName);
        } else if ((statusName != null && !statusName.isBlank())){
            result = ticketRepository.findByStatus_statusTypeContainingIgnoreCase(statusName);
        } else {
            result = ticketRepository.findAll();
        }

        return result;
    }

}


