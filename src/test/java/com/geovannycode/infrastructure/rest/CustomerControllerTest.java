package com.geovannycode.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geovannycode.application.dto.CustomerDTO;
import com.geovannycode.domain.port.in.CustomerUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerUseCase customerUseCase;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CustomerDTO customerRequest;
    private CustomerDTO customerResponse;

    @BeforeEach
    void setUp() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        try (InputStream requestStream = new ClassPathResource("json/customer-request.json").getInputStream();
             InputStream responseStream = new ClassPathResource("json/customer-response.json").getInputStream()) {

            customerRequest = objectMapper.readValue(requestStream, CustomerDTO.class);
            customerResponse = objectMapper.readValue(responseStream, CustomerDTO.class);
        }
    }

    @Test
    void createCustomer_Success() throws Exception {
        // Given
        when(customerUseCase.createCustomer(any(CustomerDTO.class))).thenReturn(customerResponse);

        // When & Then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(customerResponse.getId()))
                .andExpect(jsonPath("$.firstName").value(customerResponse.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(customerResponse.getLastName()));
    }

    @Test
    void updateCustomer_Success() throws Exception {
        // Given
        when(customerUseCase.updateCustomer(anyLong(), any(CustomerDTO.class))).thenReturn(customerResponse);

        // When & Then
        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerResponse.getId()))
                .andExpect(jsonPath("$.firstName").value(customerResponse.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(customerResponse.getLastName()));
    }

    @Test
    void deleteCustomer_Success() throws Exception {
        // Given
        doNothing().when(customerUseCase).deleteCustomer(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCustomerById_Success() throws Exception {
        // Given
        when(customerUseCase.findCustomerById(anyLong())).thenReturn(Optional.of(customerResponse));

        // When & Then
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerResponse.getId()))
                .andExpect(jsonPath("$.firstName").value(customerResponse.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(customerResponse.getLastName()));
    }

    @Test
    void getCustomerById_NotFound() throws Exception {
        // Given
        when(customerUseCase.findCustomerById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listCustomers_Success() throws Exception {
        // Given
        when(customerUseCase.listCustomers()).thenReturn(Arrays.asList(customerResponse));

        // When & Then
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(customerResponse.getId()))
                .andExpect(jsonPath("$[0].firstName").value(customerResponse.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(customerResponse.getLastName()));
    }
}
