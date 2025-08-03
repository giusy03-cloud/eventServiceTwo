package com.dipartimento.eventservice.repository;

import com.dipartimento.eventservice.domain.Event;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByNameContainingIgnoreCase(String name);

    List<Event> findByLocationContainingIgnoreCase(String location);

    Page<Event> findAll(Pageable pageable);

    // Metodi paginati
    Page<Event> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Event> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    Page<Event> findByStartDateAfter(LocalDateTime dateTime, Pageable pageable);

    Page<Event> findByNameContainingIgnoreCaseAndStartDateAfter(String name, LocalDateTime dateTime, Pageable pageable);

    Page<Event> findByLocationContainingIgnoreCaseAndStartDateAfter(String location, LocalDateTime dateTime, Pageable pageable);


    @Transactional
    @Modifying
    @Query("DELETE FROM Event e WHERE e.startDate < :dateTime")
    void deleteByStartDateBefore(LocalDateTime dateTime);


}
