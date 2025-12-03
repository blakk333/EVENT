package com.eventaro.Eventaro.application.agency.mapper;

import com.eventaro.Eventaro.application.agency.dto.CorporateClientRequest;
import com.eventaro.Eventaro.application.agency.dto.CorporateClientResponse;
import com.eventaro.Eventaro.domain.agency.CorporateClient;
import org.springframework.stereotype.Component;

@Component
public class CorporateClientMapper {

    public CorporateClientResponse toResponse(CorporateClient entity) {
        if (entity == null) return null;

        CorporateClientResponse dto = new CorporateClientResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setContactPerson(entity.getContactPerson());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        dto.setType(entity.getType());
        dto.setNegotiatedDiscount(entity.getNegotiatedDiscount());
        dto.setBookingCode(entity.getBookingCode());
        return dto;
    }

    public CorporateClient toEntity(CorporateClientRequest request) {
        if (request == null) return null;

        // ID und BookingCode werden nicht vom Request gesetzt (werden generiert)
        return new CorporateClient(
                request.getName(),
                request.getType(),
                request.getNegotiatedDiscount()
        );
        // Hinweis: Andere Felder wie ContactPerson müssen ggf. noch per Setter gesetzt werden,
        // je nachdem welchen Konstruktor Sie in der Entity haben.
        // Alternativ: Leeren Konstruktor nutzen und alles setten.
    }

    // Hilfsmethode für Updates
    public void updateEntityFromRequest(CorporateClient entity, CorporateClientRequest request) {
        entity.setName(request.getName());
        entity.setContactPerson(request.getContactPerson());
        entity.setEmail(request.getEmail());
        entity.setAddress(request.getAddress());
        entity.setType(request.getType());
        entity.setNegotiatedDiscount(request.getNegotiatedDiscount());
    }
}