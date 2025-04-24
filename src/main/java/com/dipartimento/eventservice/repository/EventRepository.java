package com.dipartimento.eventservice.repository;

import com.dipartimento.eventservice.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>{
}
