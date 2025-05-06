package com.dipartimento.eventservice.controller;

import com.dipartimento.eventservice.domain.Event;
import com.dipartimento.eventservice.domain.EventStatus;
import com.dipartimento.eventservice.dto.ResponseMessage;
import com.dipartimento.eventservice.service.EventService;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<ResponseMessage> createEvent(@RequestBody Event event) {
        try {
            eventService.createEvent(event);
            return ResponseEntity.ok(new ResponseMessage("Evento creato con successo!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Errore nella creazione dell'evento: " + e.getMessage()));
        }
    }

    // Aggiungi un log nella richiesta GET
    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents() {
        // Aggiungi questa riga per fare un log nella console
        System.out.println("Richiesta GET ricevuta per ottenere tutti gli eventi.");

        List<Event> events = eventService.getAllEvents();

        // Aggiungi un altro log per vedere gli eventi che sono stati ottenuti
        System.out.println("Eventi ottenuti: " + events.size() + " eventi trovati.");

        return ResponseEntity.ok(events);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.ok(new ResponseMessage("Evento eliminato con successo"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Errore durante l'eliminazione dell'evento"));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id);

        if (event.isPresent()) {
            return ResponseEntity.ok(event.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage("Evento con ID " + id + " non trovato."));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseMessage> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        Optional<Event> existingEvent = eventService.getEventById(id);

        if (existingEvent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage("Evento con ID " + id + " non trovato"));
        }

        Event currentEvent = existingEvent.get();

        // Controllo per eventi passati
        if (currentEvent.getStartDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage("Non è possibile modificare eventi passati"));
        }

        // Aggiornamento dell'evento
        currentEvent.setName(event.getName());
        currentEvent.setDescription(event.getDescription());
        currentEvent.setStartDate(event.getStartDate());
        currentEvent.setEndDate(event.getEndDate());
        currentEvent.setLocation(event.getLocation());
        currentEvent.setOrganizerId(event.getOrganizerId());
        currentEvent.setPrice(event.getPrice());
        currentEvent.setCapacity(event.getCapacity());
        currentEvent.setStatus(event.getStatus());

        eventService.updateEvent(currentEvent);

        return ResponseEntity.ok(new ResponseMessage("Evento aggiornato con successo"));
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<ResponseMessage> updateEventStatus(@PathVariable Long id, @RequestBody EventStatus status) {
        try {
            // Chiamata al service per aggiornare lo stato dell'evento
            eventService.updateEventStatus(id, status);
            return ResponseEntity.ok(new ResponseMessage("Stato evento aggiornato con successo"));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Errore durante l'aggiornamento dello stato dell'evento"));
        }
    }




}
