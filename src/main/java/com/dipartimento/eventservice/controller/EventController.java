package com.dipartimento.eventservice.controller;

import com.dipartimento.eventservice.domain.Event;
import com.dipartimento.eventservice.dto.EventRequest;
import com.dipartimento.eventservice.dto.EventResponseDTO;
import com.dipartimento.eventservice.dto.ResponseMessage;
import com.dipartimento.eventservice.mapper.EventMapper;
import com.dipartimento.eventservice.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    // CREATE
    @PostMapping("/create")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<ResponseMessage> createEvent(@RequestBody EventRequest eventRequest) {
        try {
            Event event = EventMapper.toEntity(eventRequest);
            eventService.createEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Evento creato con successo."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Errore durante la creazione dell'evento."));
        }
    }

    @GetMapping("/paged")
    public ResponseEntity<List<EventResponseDTO>> getPaginatedEvents(
            @RequestParam(defaultValue ="0") int page,
            @RequestParam(defaultValue = "100") int size
    ){
        Pageable pageable = PageRequest.of(page,size);
        Page<Event> eventsPage=eventService.getEventsPaginated(pageable);

        List<EventResponseDTO> response=eventsPage.getContent().stream()
                .map(EventMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // READ ALL
    @GetMapping("/all")
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        List<EventResponseDTO> response = events.stream()
                .map(EventMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id);
        return event.map(value -> ResponseEntity.ok(EventMapper.toResponseDTO(value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<ResponseMessage> updateEvent(@PathVariable Long id, @RequestBody EventRequest eventRequest) {
        Optional<Event> existingEvent = eventService.getEventById(id);
        if (existingEvent.isPresent()) {
            Event eventToUpdate = EventMapper.toEntity(eventRequest);
            eventToUpdate.setId(id);
            eventService.updateEvent(eventToUpdate);  // CORRETTO: solo un parametro
            return ResponseEntity.ok(new ResponseMessage("Evento aggiornato con successo."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Evento con ID " + id + " non trovato."));
        }
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<ResponseMessage> deleteEvent(@PathVariable Long id) {
        boolean deleted = eventService.deleteEvent(id);  // ORA RITORNA UN BOOLEAN
        if (deleted) {
            return ResponseEntity.ok(new ResponseMessage("Evento eliminato con successo."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Evento con ID " + id + " non trovato."));
        }
    }


    // SEARCH BY NAME
    @GetMapping("/search/byName")
    public ResponseEntity<List<EventResponseDTO>> getEventsByName(@RequestParam String name) {
        List<Event> events = eventService.getEventsByName(name);
        List<EventResponseDTO> response = events.stream()
                .map(EventMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // SEARCH BY LOCATION
    @GetMapping("/search/byLocation")
    public ResponseEntity<List<EventResponseDTO>> getEventsByLocation(@RequestParam String location) {
        List<Event> events = eventService.getEventsByLocation(location);
        List<EventResponseDTO> response = events.stream()
                .map(EventMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
