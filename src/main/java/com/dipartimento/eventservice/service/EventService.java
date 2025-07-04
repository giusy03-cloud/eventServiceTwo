package com.dipartimento.eventservice.service;

import com.dipartimento.eventservice.domain.Event;
import com.dipartimento.eventservice.domain.EventStatus;
import com.dipartimento.eventservice.dto.UsersAccounts;
import com.dipartimento.eventservice.repository.EventRepository;
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

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

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
        if (!checkOrganizerExists(event.getOrganizerId(), token)) {
            throw new IllegalArgumentException("Organizzatore con ID " + event.getOrganizerId() + " non trovato o non autorizzato.");
        }
        eventRepository.save(event);
    }


    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }


    public boolean deleteEvent(Long id, String token) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            return false;
        }
        Event event = eventOpt.get();

        if (!checkOrganizerExists(event.getOrganizerId(), token)) {
            System.out.println("Utente non autorizzato a cancellare evento con id " + id);
            throw new IllegalArgumentException("Non sei autorizzato a eliminare l'evento.");
        }

        eventRepository.delete(event);
        return true;
    }




    public void updateEvent(Event event) {
        eventRepository.save(event);  // Usa il metodo save per aggiornare l'evento
    }

    public void updateEventStatus(Long id, EventStatus status) {
        Optional<Event> eventOptional = eventRepository.findById(id);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();

            // Verifica se lo stato corrente dell'evento impedisce aggiornamenti
            if (event.getStatus() == EventStatus.COMPLETED || event.getStatus() == EventStatus.CANCELLED) {
                throw new IllegalStateException("Non è possibile cambiare lo stato di un evento completato o cancellato.");
            }

            // Impostazione del nuovo stato
            event.setStatus(status);
            eventRepository.save(event);  // Salviamo l'evento aggiornato nel database
        } else {
            throw new IllegalArgumentException("Evento con ID " + id + " non trovato.");
        }
    }

    public List<Event> getEventsByName(String name){
        return eventRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Event> getEventsByLocation(String location){
        return eventRepository.findByLocationContainingIgnoreCase(location);
    }

    public Page<Event> getEventsPaginated(Pageable pageable){
        return eventRepository.findAll(pageable);
    }


}
