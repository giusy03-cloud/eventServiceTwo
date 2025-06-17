package com.dipartimento.eventservice.mapper;

import com.dipartimento.eventservice.domain.Event;
import com.dipartimento.eventservice.domain.EventStatus;
import com.dipartimento.eventservice.dto.EventRequest;
import com.dipartimento.eventservice.dto.EventResponseDTO;

public class EventMapper {

    public static Event toEntity(EventRequest request) {
        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setLocation(request.getLocation());
        event.setOrganizerId(request.getOrganizerId());
        event.setPrice(request.getPrice());
        event.setCapacity(request.getCapacity());
        event.setStatus(EventStatus.valueOf(request.getStatus()));
        return event;
    }

    public static EventResponseDTO toResponseDTO(Event event) {
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
}
