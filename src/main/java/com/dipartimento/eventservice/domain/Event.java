package com.dipartimento.eventservice.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name= "events")
@Getter
@Setter
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="event_id")
    private Long id;

    @Column(name="name",nullable = false,length =255)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Column(name = "organizer_id", nullable = false)  // Cambia solo l'ID dell'organizzatore
    private UUID organizerId;  // Usando UUID per l'ID dell'organizzatore


    @Column(name = "price")
    private Double price;  // Prezzo dell'evento (se applicabile)

    @Column(name = "capacity")
    private Integer capacity;  // Capacità massima di partecipanti

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;


}
