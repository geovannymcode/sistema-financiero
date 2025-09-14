package com.geovannycode.application.mapper;

import com.geovannycode.application.dto.CustomerDTO;
import com.geovannycode.domain.model.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {

    public CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerDTO.builder()
                .id(customer.getId())
                .identificationType(customer.getIdentificationType())
                .identificationNumber(customer.getIdentificationNumber())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .birthDate(customer.getBirthDate())
                .build();
    }

    public Customer toEntity(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }

        return Customer.builder()
                .id(dto.getId())
                .identificationType(dto.getIdentificationType())
                .identificationNumber(dto.getIdentificationNumber())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .birthDate(dto.getBirthDate())
                .build();
    }

    public List<CustomerDTO> toDTOList(List<Customer> customers) {
        if (customers == null) {
            return null;
        }

        return customers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
