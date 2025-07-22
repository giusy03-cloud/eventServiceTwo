package com.dipartimento.eventservice.service;

import com.dipartimento.eventservice.domain.Event;
import com.dipartimento.eventservice.domain.EventStatus;
import com.dipartimento.eventservice.dto.UsersAccounts;
import com.dipartimento.eventservice.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;
import com.dipartimento.eventservice.security.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String USER_SERVICE_URL = "http://localhost:8080/api/users/";
    private final String AUTH_ME_URL = "http://localhost:8080/auth/me";

    public boolean checkOrganizerExists(Long organizerId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = AUTH_ME_URL;  // Usa l'endpoint corretto
            ResponseEntity<UsersAccounts> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    UsersAccounts.class
            );

            if (response.getBody() != null) {
                UsersAccounts user = response.getBody();
                System.out.println("Utente autenticato: " + user.getUsername() + ", ruolo: " + user.getRole() + ", id: " + user.getId());

                return "ORGANIZER".equalsIgnoreCase(user.getRole())
                        && organizerId != null
                        && organizerId.equals(user.getId());
            }

            return false;
        } catch (HttpClientErrorException e) {
            System.err.println("Errore HTTP: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void createEvent(Event event, String token) {
        logger.info("Richiesta creazione evento da parte dell'utente con token: {}", token);

        if (!checkOrganizerExists(event.getOrganizerId(), token)) {
            logger.warn("Organizzatore NON autorizzato: id={}", event.getOrganizerId());
            throw new IllegalArgumentException("Organizzatore con ID " + event.getOrganizerId() + " non trovato o non autorizzato.");
        }

        LocalDateTime now = LocalDateTime.now();
        event.setCreatedAt(now);
        event.setUpdatedAt(now);
        event.setCreatedBy(event.getOrganizerId());
        event.setUpdatedBy(event.getOrganizerId());

        logger.info("Salvataggio evento: {}", event.getName());
        eventRepository.save(event);
    }


    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }




    public boolean deleteEvent(Long id, String token) {
        logger.info("Richiesta eliminazione evento ID={} da parte di token={}", id, token);

        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            logger.warn("Evento non trovato per eliminazione: ID={}", id);
            return false;
        }

        Event event = eventOpt.get();

        // Log evento e organizerId
        logger.info("Evento trovato: ID={}, OrganizerId={}", event.getId(), event.getOrganizerId());

        // Usa metodo statico della classe JwtUtil per estrarre userId dal token
        Long userIdFromToken;
        try {
            userIdFromToken = JwtUtil.extractUserId(token);
            logger.info("UserId estratto dal token: {}", userIdFromToken);
        } catch (Exception e) {
            logger.error("Errore estrazione userId dal token: {}", e.getMessage());
            throw new IllegalArgumentException("Token non valido o userId non trovato.");
        }

        // Verifica che l'utente sia il creatore dell'evento
        if (!event.getOrganizerId().equals(userIdFromToken)) {
            logger.warn("Tentativo non autorizzato di eliminazione evento ID={} da parte di utente ID={}", id, userIdFromToken);
            throw new IllegalArgumentException("Non sei autorizzato ad eliminare questo evento.");
        }

        eventRepository.delete(event);
        logger.info("Evento eliminato con successo: ID={}", id);
        return true;
    }



    public void updateEvent(Event event) {
        logger.info("Richiesta aggiornamento evento con ID: {}", event.getId());

        Optional<Event> existingEventOpt = eventRepository.findById(event.getId());
        if (existingEventOpt.isEmpty()) {
            logger.error("Evento non trovato per aggiornamento: ID={}", event.getId());
            throw new IllegalArgumentException("Evento con ID " + event.getId() + " non trovato.");
        }

        Event existingEvent = existingEventOpt.get();

        event.setCreatedAt(existingEvent.getCreatedAt());
        event.setCreatedBy(existingEvent.getCreatedBy());
        event.setUpdatedAt(LocalDateTime.now());
        event.setUpdatedBy(existingEvent.getOrganizerId());

        logger.info("Evento aggiornato: ID={}, nome={}", event.getId(), event.getName());
        eventRepository.save(event);
    }



    public Page<Event> getEventsByName(String name, Pageable pageable) {
        return eventRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<Event> getEventsByLocation(String location, Pageable pageable) {
        return eventRepository.findByLocationContainingIgnoreCase(location, pageable);
    }

    public Page<Event> getEventsPaginated(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }
    public List<Event> getEventsByIds(List<Long> ids) {
        return eventRepository.findAllById(ids);
    }



}
