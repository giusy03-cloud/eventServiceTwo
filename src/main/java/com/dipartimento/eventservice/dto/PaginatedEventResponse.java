package com.dipartimento.eventservice.dto;

import java.util.List;

public class PaginatedEventResponse {
    private List<EventResponseDTO> content;
    private long totalElements;

    public PaginatedEventResponse(List<EventResponseDTO> content, long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
    }

    public List<EventResponseDTO> getContent() {
        return content;
    }

    public void setContent(List<EventResponseDTO> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
