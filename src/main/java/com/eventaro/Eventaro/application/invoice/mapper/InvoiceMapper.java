package com.eventaro.Eventaro.application.invoice.mapper;

import com.eventaro.Eventaro.application.invoice.dto.InvoiceItemDTO;
import com.eventaro.Eventaro.application.invoice.dto.InvoiceResponseDTO;
import com.eventaro.Eventaro.domain.invoice.Invoice;
import com.eventaro.Eventaro.domain.invoice.InvoiceItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class InvoiceMapper {

    public InvoiceResponseDTO toResponse(Invoice entity) {
        if (entity == null) return null;

        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(entity.getId());
        dto.setBookingRef(entity.getBookingRef());
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setRecipientName(entity.getRecipientName());
        dto.setRecipientAddress(entity.getRecipientAddress());
        dto.setDate(entity.getDate());
        dto.setStatus(entity.getStatus());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setAdvancePayment(entity.getAdvancePayment());

        // Berechnete Werte aus der Entity übernehmen
        dto.setTotalNet(entity.getTotalNet());
        dto.setTotalVat(entity.getTotalVat());
        dto.setTotalGross(entity.getTotalGross());
        dto.setRemainingAmount(entity.getRemainingAmount());

        // Liste mappen
        dto.setItems(entity.getItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private InvoiceItemDTO toItemDTO(InvoiceItem item) {
        InvoiceItemDTO dto = new InvoiceItemDTO();
        dto.setId(item.getId());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalNet(item.getTotalNet());
        return dto;
    }
}