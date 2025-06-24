package com.dipartimento.eventservice.service;

import com.dipartimento.eventservice.domain.Event;
import com.dipartimento.eventservice.domain.EventStatus;
import com.dipartimento.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public void createEvent(Event event) {
        eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public boolean deleteEvent(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
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
