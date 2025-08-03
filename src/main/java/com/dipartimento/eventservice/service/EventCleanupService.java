package com.dipartimento.eventservice.service;

import com.dipartimento.eventservice.repository.EventRepository;
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

    // Ogni giorno a mezzanotte elimina eventi con startDate antecedente a oggi
    @Scheduled(cron = "0 0 0 * * ?")
    public void deletePastEvents() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        eventRepository.deleteByStartDateBefore(todayStart);
        System.out.println("Eventi passati eliminati fino a: " + todayStart);
    }
}
