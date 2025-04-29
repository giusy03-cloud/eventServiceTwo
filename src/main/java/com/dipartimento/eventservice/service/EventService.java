package com.dipartimento.eventservice.service;

import com.dipartimento.eventservice.domain.Event;
import com.dipartimento.eventservice.domain.EventStatus;
import com.dipartimento.eventservice.dto.EventDTO;
import com.dipartimento.eventservice.dto.EventResponseDTO;
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

    // Metodo per mappare un EventDTO in un Event
    private Event mapToEntity(EventDTO eventDTO) {
        Event event = new Event();
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        event.setLocation(eventDTO.getLocation());
        event.setOrganizerId(eventDTO.getOrganizerId());
        event.setPrice(eventDTO.getPrice());
        event.setCapacity(eventDTO.getCapacity());
        event.setStatus(eventDTO.getStatus());
        return event;
    }

    // Metodo per mappare un Event in un EventResponseDTO
    private EventResponseDTO mapToDTO(Event event) {
        return new EventResponseDTO(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getLocation(),
                event.getOrganizerId(),
                event.getPrice(),
                event.getCapacity(),
                event.getStatus()
        );
    }

    // Crea un nuovo evento e restituisci il DTO di risposta
    @Transactional
    public EventResponseDTO createEvent(EventDTO eventDTO) {
        Event event = mapToEntity(eventDTO);
        event.setStatus(EventStatus.ACTIVE); // Imposta lo stato come "ACTIVE" di default
        event = eventRepository.save(event);

        return mapToDTO(event); // Restituisce il DTO di risposta
    }

    // Modifica un evento esistente
    @Transactional
    public EventResponseDTO updateEvent(Long id, EventDTO eventDTO) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isPresent()) {
            Event existingEvent = optionalEvent.get();
            // Mappa i dati dal DTO all'oggetto esistente
            existingEvent.setName(eventDTO.getName());
            existingEvent.setDescription(eventDTO.getDescription());
            existingEvent.setStartDate(eventDTO.getStartDate());
            existingEvent.setEndDate(eventDTO.getEndDate());
            existingEvent.setLocation(eventDTO.getLocation());
            existingEvent.setOrganizerId(eventDTO.getOrganizerId());
            existingEvent.setPrice(eventDTO.getPrice());
            existingEvent.setCapacity(eventDTO.getCapacity());
            existingEvent.setStatus(eventDTO.getStatus());
            eventRepository.save(existingEvent);

            return mapToDTO(existingEvent); // Restituisce il DTO aggiornato
        }
        return null;
    }

    // Elimina un evento
    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    // Recupera un evento per ID
    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        return event != null ? mapToDTO(event) : null;
    }

    // Recupera tutti gli eventi
    public List<EventResponseDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(this::mapToDTO)
                .toList(); // Restituisce una lista di DTO
    }
}
