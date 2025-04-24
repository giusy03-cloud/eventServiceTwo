package com.dipartimento.eventservice.service;

import com.dipartimento.eventservice.domain.Event;
import com.dipartimento.eventservice.domain.EventStatus;
import com.dipartimento.eventservice.dto.EventDTO;
import com.dipartimento.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    // Crea un nuovo evento
    @Transactional
    public Event createEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        event.setLocation(eventDTO.getLocation());
        event.setPrice(eventDTO.getPrice());
        event.setCapacity(eventDTO.getCapacity());
        event.setStatus(EventStatus.ACTIVE);

        return eventRepository.save(event);
    }

    // Modifica un evento esistente
    @Transactional
    public Event updateEvent(Long id, EventDTO eventDTO) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            event.setName(eventDTO.getName());
            event.setDescription(eventDTO.getDescription());
            event.setStartDate(eventDTO.getStartDate());
            event.setEndDate(eventDTO.getEndDate());
            event.setLocation(eventDTO.getLocation());
            event.setPrice(eventDTO.getPrice());
            event.setCapacity(eventDTO.getCapacity());
            event.setStatus(eventDTO.getStatus());
            return eventRepository.save(event);
        }
        return null; // O lancia una RuntimeException
    }

    // Elimina un evento
    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    // Recupera un evento per ID
    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    // Recupera tutti gli eventi
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}
