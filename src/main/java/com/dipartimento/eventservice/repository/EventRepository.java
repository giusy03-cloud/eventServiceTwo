package com.dipartimento.eventservice.repository;

import com.dipartimento.eventservice.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByNameContainingIgnoreCase(String name);

    List<Event> findByLocationContainingIgnoreCase(String location);

    Page<Event> findAll(Pageable pageable);

    // Metodi paginati
    Page<Event> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Event> findByLocationContainingIgnoreCase(String location, Pageable pageable);
}
