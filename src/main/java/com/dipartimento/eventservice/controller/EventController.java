package com.dipartimento.eventservice.controller;

import com.dipartimento.eventservice.dto.EventDTO;
import com.dipartimento.eventservice.dto.EventResponseDTO;
import com.dipartimento.eventservice.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;


    @PostMapping("/create")  // Specifica l'endpoint per la creazione dell'evento
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody EventDTO eventDTO) {
        EventResponseDTO eventResponse = eventService.createEvent(eventDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventResponse);
    }


    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        EventResponseDTO updatedEvent = eventService.updateEvent(id, eventDTO);
        if (updatedEvent != null) {
            return ResponseEntity.ok(updatedEvent);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable Long id) {
        EventResponseDTO eventResponse = eventService.getEventById(id);
        if (eventResponse != null) {
            return ResponseEntity.ok(eventResponse);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping
    public List<EventResponseDTO> getAllEvents() {
        return eventService.getAllEvents();
    }
}
