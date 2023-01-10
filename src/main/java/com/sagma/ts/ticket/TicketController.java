package com.sagma.ts.ticket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
@Transactional
public class TicketController {
    @Autowired
    private TicketRepository ticketRepository;
    
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Endpoint to retrieve all active tickets
    @RequestMapping(value = "/tickets",
        method = { RequestMethod.GET },
        produces = "application/json;charset=UTF-8")
    @ResponseBody  
    public Iterable<Ticket> getTicketsAll(HttpServletRequest request, HttpServletResponse response) {
        List<Ticket> ticketList = ticketRepository.findAllByOrderByIdAsc();
        // Iterate all tickets on the ordered list by id and store the transiet ticket order number
        for (Integer i=0; i<ticketList.size(); i++)
            ticketList.get(i).setOrderNr(i);
        return ticketList;
    }
    
    // Endpoint to generate new ticket
    @RequestMapping(value = "/tickets",
        method = { RequestMethod.POST },
        produces = "application/json;charset=UTF-8")
    @ResponseBody   
    public Ticket postTicket(HttpServletRequest request, HttpServletResponse response) {
        Ticket ticket = new Ticket();
        ticket.setTimestamp(LocalDateTime.now().format(formatter));
        // Set the ticket order number as transient data to be returned 
        ticket.setOrderNr((int)ticketRepository.count());
        ticketRepository.save(ticket);
        return ticket;
    }
    
    // Endpoint to generate current ticket
    @RequestMapping(value = "/tickets/current",
        method = { RequestMethod.GET },
        produces = "application/json;charset=UTF-8")
    @ResponseBody   
    public Ticket getTicketsCurrent(HttpServletRequest request, HttpServletResponse response) {
        List<Ticket> ticketList = ticketRepository.findFirstByOrderById();
        // throw 404 if the current ticket is not available
        if (ticketList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ticket not found");
        else
            return ticketList.get(0); // current ticket has the ticket order number always set to 0
    }
    
    // Endpoint to delete last ticket
    @RequestMapping(value = "/tickets/current",
        method = { RequestMethod.DELETE },
        produces = "application/json;charset=UTF-8")
    @ResponseBody   
    public Ticket deleteTicketCurrent(HttpServletRequest request, HttpServletResponse response) {
        // If current ticket is not available, throws 404 in getTicketsCurrent
        Ticket ticket = getTicketsCurrent(request, response);
        ticketRepository.deleteById(ticket.getId());
        return ticket;
    }
}
