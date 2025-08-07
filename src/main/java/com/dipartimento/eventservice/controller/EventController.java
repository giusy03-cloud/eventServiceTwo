package com.dipartimento.eventservice.controller;

import com.dipartimento.eventservice.domain.Event;
import com.dipartimento.eventservice.dto.*;
import com.dipartimento.eventservice.mapper.EventMapper;
import com.dipartimento.eventservice.repository.EventRepository;
import com.dipartimento.eventservice.service.EventCleanupService;
import com.dipartimento.eventservice.service.EventService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.Optional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventCleanupService eventCleanupService;


    @PostMapping("/create")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<ResponseMessage> createEvent(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody EventRequest eventRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage("Errore di validazione: " + String.join(", ", errors)));
        }

        try {
            String token = authorizationHeader.replace("Bearer ", "");
            Event event = EventMapper.toEntity(eventRequest);
            eventService.createEvent(event, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Evento creato con successo."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Errore durante la creazione dell'evento."));
        }
    }




    @GetMapping("/paged")
    public ResponseEntity<PaginatedEventResponse> getPaginatedEvents(
            @RequestParam(defaultValue ="0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = eventService.getEventsPaginated(pageable);

        List<EventResponseDTO> response = eventsPage.getContent().stream()
                .map(EventMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PaginatedEventResponse(response, eventsPage.getTotalElements()));
    }


    // READ ALL
    @GetMapping("/all")
    @PreAuthorize("hasRole('PARTICIPANT') or hasRole('ORGANIZER')")
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        List<EventResponseDTO> response = events.stream()
                .map(EventMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id);
        return event.map(value -> ResponseEntity.ok(EventMapper.toResponseDTO(value)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Endpoint pubblico per controllo esistenza evento da altri microservizi


    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPublicEvent(@PathVariable Long id) {
        Optional<Event> event = eventRepository.findByIdAndArchivedFalse(id);

        if (event.isPresent()) {
            return ResponseEntity.ok(event.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento non trovato o archiviato");
        }
    }

    // Endpoint interno accessibile solo dal ReviewService
    @GetMapping("/internal/{id}")
    public ResponseEntity<Event> getEventEvenIfArchived(@PathVariable Long id) {
        Optional<Event> event = eventRepository.findById(id);

        if (event.isPresent()) {
            return ResponseEntity.ok(event.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }



    @GetMapping("/archiveNow")
    public ResponseEntity<String> archiveNow() {
        eventCleanupService.archiveOldEvents();
        return ResponseEntity.ok("Archiviazione eventi passati eseguita");
    }



    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<ResponseMessage> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest eventRequest,
            BindingResult bindingResult,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest()
                    .body(new ResponseMessage("Errore di validazione: " + String.join(", ", errors)));
        }

        String token = authorizationHeader.replace("Bearer ", "");

        Optional<Event> existingEvent = eventService.getEventById(id);
        if (existingEvent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage("Evento con ID " + id + " non trovato."));
        }

        Event event = existingEvent.get();

        // Verifica che l'utente sia il proprietario e abbia ruolo ORGANIZER
        if (!eventService.checkOrganizerExists(event.getOrganizerId(), token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessage("Non sei autorizzato ad aggiornare questo evento."));
        }

        // Prosegui con l'aggiornamento
        Event eventToUpdate = EventMapper.toEntity(eventRequest);
        eventToUpdate.setId(id);

        // Mantieni i dati di creazione originali
        eventToUpdate.setCreatedAt(event.getCreatedAt());
        eventToUpdate.setCreatedBy(event.getCreatedBy());

        eventService.updateEvent(eventToUpdate);

        return ResponseEntity.ok(new ResponseMessage("Evento aggiornato con successo."));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteEvent(@PathVariable Long id,
                                                       @RequestHeader("Authorization") String authHeader) {
        System.out.println("DELETE /events/delete/" + id + " chiamato");  // Controllo semplice
        String token = authHeader.replace("Bearer ", "");
        try {
            boolean deleted = eventService.deleteEvent(id, token);
            if (deleted) {
                return ResponseEntity.ok(new ResponseMessage("Evento eliminato con successo."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseMessage("Evento con ID " + id + " non trovato."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessage(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Errore durante l'eliminazione dell'evento."));
        }
    }





    @GetMapping("/search/byName")
    public ResponseEntity<PaginatedEventResponse> getEventsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new PaginatedEventResponse(List.of(), 0));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventService.getEventsByName(name, pageable);

        List<EventResponseDTO> response = events.getContent().stream()
                .map(EventMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new PaginatedEventResponse(response, events.getTotalElements()));
    }
    // SEARCH BY LOCATION


    @GetMapping("/search/byLocation")
    public ResponseEntity<PaginatedEventResponse> getEventsByLocation(
            @RequestParam String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (location == null || location.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new PaginatedEventResponse(List.of(), 0));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventService.getEventsByLocation(location, pageable);

        List<EventResponseDTO> response = events.getContent().stream()
                .map(EventMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new PaginatedEventResponse(response, events.getTotalElements()));
    }

    @PostMapping("/byIds")
    @PreAuthorize("hasRole('PARTICIPANT') or hasRole('ORGANIZER')")
    public ResponseEntity<List<EventResponseDTO>> getEventsByIds(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody List<Long> ids) {

        // Assumi che eventService abbia un metodo per recuperare eventi per lista ID
        List<Event> events = eventService.getEventsByIds(ids);

        List<EventResponseDTO> response = events.stream()
                .map(EventMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/public/details/{id}")
    public ResponseEntity<EventResponseDTO> getEventDetailsPublic(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id);
        if (event.isPresent()) {
            return ResponseEntity.ok(EventMapper.toResponseDTO(event.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/public/byIds")
    public ResponseEntity<List<EventResponseDTO>> getEventsByIdsPublic(@RequestBody List<Long> ids) {
        List<Event> events = eventService.getEventsByIds(ids);
        List<EventResponseDTO> response = events.stream()
                .map(EventMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}/export", produces = "text/calendar")
    public ResponseEntity<String> exportEvent(@PathVariable Long id) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = eventOpt.get();

        DateTimeFormatter icalFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

        // Converti LocalDateTime a UTC per esportazione corretta
        ZonedDateTime startUtc = event.getStartDate().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime endUtc = event.getEndDate().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);

        String icsContent = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "BEGIN:VEVENT\n" +
                "UID:" + UUID.randomUUID() + "\n" +
                "DTSTAMP:" + nowUtc.format(icalFormatter) + "\n" +
                "DTSTART:" + startUtc.format(icalFormatter) + "\n" +
                "DTEND:" + endUtc.format(icalFormatter) + "\n" +
                "SUMMARY:" + event.getName() + "\n" +
                "DESCRIPTION:" + event.getDescription() + "\n" +
                "LOCATION:" + event.getLocation() + "\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/calendar"));
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename("evento.ics").build());

        return new ResponseEntity<>(icsContent, headers, HttpStatus.OK);
    }

    @GetMapping("/archived")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('PARTICIPANT')")
    public ResponseEntity<List<EventResponseDTO>> getArchivedEvents() {
        List<Event> archivedEvents = eventService.getArchivedEvents();
        List<EventResponseDTO> response = archivedEvents.stream()
                .map(EventMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/future")
    @PreAuthorize("hasRole('PARTICIPANT') or hasRole('ORGANIZER')")
    public List<Event> getFutureEvents() {
        return eventService.getFutureEvents();
    }

    @GetMapping("/future/paginated")
    @PreAuthorize("hasRole('PARTICIPANT') or hasRole('ORGANIZER')")
    public Page<Event> getFutureEventsPaginated(Pageable pageable) {
        return eventService.getFutureEvents(pageable);
    }





}
