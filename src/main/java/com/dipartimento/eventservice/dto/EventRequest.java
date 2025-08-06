package com.dipartimento.eventservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.dipartimento.eventservice.validation.ValidDateRange;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@ValidDateRange
public class EventRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 255, message = "Il nome non può superare 255 caratteri")
    private String name;

    @Size(max = 1000, message = "La descrizione non può superare 1000 caratteri")
    private String description;

    @NotNull(message = "La data di inizio è obbligatoria")
    private LocalDateTime startDate;

    @NotNull(message = "La data di fine è obbligatoria")
    private LocalDateTime endDate;

    @NotBlank(message = "La location è obbligatoria")
    @Size(max = 255, message = "La location non può superare 255 caratteri")
    private String location;

    @NotNull(message = "L'organizerId è obbligatorio")
    private Long organizerId;

    @PositiveOrZero(message = "Il prezzo deve essere positivo o zero")
    private Double price;

    @Positive(message = "La capacità deve essere un numero positivo")
    private Integer capacity;

    @NotBlank(message = "Lo status è obbligatorio")
    private String status; // oppure meglio usare enum EventStatus se possibile


    @Size(max = 1000, message = "L'URL dell'immagine non può superare 1000 caratteri")
    private String imageUrl;
    // Aggiungi il campo
    private Boolean archived;

    // Getter e setter
    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
