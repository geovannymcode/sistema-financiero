package com.geovannycode.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
public class CustomerDTO {

    private final Long id;

    @NotBlank(message = "Identification type is required")
    private final String identificationType;

    @NotBlank(message = "Identification number is required")
    private final String identificationNumber;

    @NotBlank(message = "First name is required")
    @Size(min = 2, message = "First name must have at least 2 characters")
    private final String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, message = "Last name must have at least 2 characters")
    private final String lastName;

    @Email(message = "Must be a valid email address")
    private final String email;

    @Past(message = "Birth date must be in the past")
    private final LocalDate birthDate;

    @Builder
    public CustomerDTO(Long id, String identificationType, String identificationNumber,
                       String firstName, String lastName, String email, LocalDate birthDate) {
        this.id = id;
        this.identificationType = identificationType;
        this.identificationNumber = identificationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
    }
}
