package com.dipartimento.eventservice.service;

import com.dipartimento.eventservice.repository.EventRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventCleanupService {

    private final EventRepository eventRepository;

    public EventCleanupService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }


    /*
    @Scheduled(cron = "0 59 23 * * ?")
    @Transactional
    public void archiveEventsStartingTomorrow() {
        LocalDateTime start = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime end = start.plusDays(1); // Giorno dopo

        eventRepository.archiveEventsBetween(start, end);
        System.out.println("✅ Eventi archiviati: " + start.toLocalDate());
    }

     */


    @Transactional
    public void archiveOldEvents() {
        LocalDateTime midnightTomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        int updatedCount = eventRepository.archiveEventsBetween(midnightTomorrow);
        System.out.println("✅ Archiviati eventi prima di " + midnightTomorrow + ": " + updatedCount);
    }



    @Scheduled(fixedRate = 10000) // ogni 10 secondi
    @Transactional
    public void archiveOldEventsScheduled() {
        LocalDateTime now = LocalDate.now().plusDays(1).atStartOfDay();
        int updatedCount = eventRepository.archiveEventsBefore(now);
        System.out.println("✅ Archiviati eventi fino a " + now + ": " + updatedCount);
    }







}