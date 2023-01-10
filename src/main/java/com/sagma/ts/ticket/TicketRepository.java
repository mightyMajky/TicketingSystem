package com.sagma.ts.ticket;

import java.util.List;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import jakarta.persistence.LockModeType;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
    public List<Ticket> findAllByOrderByIdAsc();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public List<Ticket> findFirstByOrderById();
}
