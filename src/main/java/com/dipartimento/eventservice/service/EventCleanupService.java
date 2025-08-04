package com.dipartimento.eventservice.service;

import com.dipartimento.eventservice.repository.EventRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class EventCleanupService {

    private final EventRepository eventRepository;

    public EventCleanupService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }



    @Scheduled(cron = "0 59 23 * * ?") // ogni giorno alle 23:59
// ogni minuto
// ogni giorno alle 23:59
    public void deleteEventsStartingTomorrow() {
        LocalDateTime tomorrowStart = LocalDate.now().plusDays(1).atStartOfDay();       // 2025-08-05T00:00
        LocalDateTime dayAfterTomorrowStart = LocalDate.now().plusDays(2).atStartOfDay(); // 2025-08-06T00:00

        eventRepository.deleteByStartDateBetween(tomorrowStart, dayAfterTomorrowStart);

        System.out.println("Eventi del giorno " + tomorrowStart.toLocalDate() + " eliminati alle 23:59");
    }

    // ✅ Cleanup all'avvio dell'app
    @PostConstruct
    public void cleanEventsOnStartup() {
        LocalDateTime todayMidnight = LocalDate.now().atStartOfDay().plusDays(1); // 00:00 del giorno dopo
        eventRepository.deleteByStartDateBefore(todayMidnight);
        System.out.println("Cleanup eseguito all'avvio per eventi con startDate prima di: " + todayMidnight);
    }



}
