package com.eventaro.Eventaro.application.address.mapper;

import com.eventaro.Eventaro.application.address.dto.AddressDTO;
import com.eventaro.Eventaro.domain.address.Address;

// Änderung hier: Klasse heißt jetzt AddressDTOMapper (passend zur Datei)
public final class AddressDTOMapper {

    // Änderung hier: Konstruktorname angepasst
    private AddressDTOMapper() {
    }

    public static Address toDomain(AddressDTO dto) {
        if (dto == null) return null;

        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());

        if (dto.getHouseNumber() != null && !dto.getHouseNumber().isBlank()) {
            address.setHouseNumber(parseInteger(dto.getHouseNumber().trim()));
        }

        if (dto.getPostalCode() != null && !dto.getPostalCode().isBlank()) {
            address.setZipCode(parseInteger(dto.getPostalCode().trim()));
        }

        return address;
    }

    public static AddressDTO toDto(Address address) {
        if (address == null) return null;

        AddressDTO dto = new AddressDTO();
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setCountry(address.getCountry());

        if (address.getHouseNumber() != null) {
            dto.setHouseNumber(String.valueOf(address.getHouseNumber()));
        }

        if (address.getZipCode() != null) {
            dto.setPostalCode(String.valueOf(address.getZipCode()));
        }

        return dto;
    }

    private static Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Numeric value expected, but was: " + value);
        }
    }
}