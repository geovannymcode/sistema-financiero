package com.geovannycode.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geovannycode.application.dto.CreateTransactionDTO;
import com.geovannycode.application.dto.TransactionDTO;
import com.geovannycode.domain.port.in.TransactionUseCase;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Mock
    private TransactionUseCase transactionUseCase;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CreateTransactionDTO transactionRequest;
    private TransactionDTO transactionResponse;

    @BeforeEach
    void setUp() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        try (InputStream requestStream = new ClassPathResource("json/transaction-request.json").getInputStream();
             InputStream responseStream = new ClassPathResource("json/transaction-response.json").getInputStream()) {

            transactionRequest = objectMapper.readValue(requestStream, CreateTransactionDTO.class);
            transactionResponse = objectMapper.readValue(responseStream, TransactionDTO.class);
        }
    }

    @Test
    void createTransaction_Success() throws Exception {
        // Given
        when(transactionUseCase.createTransaction(any(CreateTransactionDTO.class))).thenReturn(transactionResponse);

        // When & Then
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transactionResponse.getId()))
                .andExpect(jsonPath("$.transactionType").value(transactionResponse.getTransactionType().toString()))
                .andExpect(jsonPath("$.amount").value(transactionResponse.getAmount().doubleValue()));
    }

    @Test
    void getTransactionById_Success() throws Exception {
        // Given
        when(transactionUseCase.findTransactionById(anyLong())).thenReturn(Optional.of(transactionResponse));

        // When & Then
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionResponse.getId()))
                .andExpect(jsonPath("$.transactionType").value(transactionResponse.getTransactionType().toString()));
    }

    @Test
    void getTransactionById_NotFound() throws Exception {
        // Given
        when(transactionUseCase.findTransactionById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTransactionsByAccount_Success() throws Exception {
        // Given
        when(transactionUseCase.getTransactionsByAccount(anyString())).thenReturn(Arrays.asList(transactionResponse));

        // When & Then
        mockMvc.perform(get("/api/transactions/accounts/5312345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionResponse.getId()))
                .andExpect(jsonPath("$[0].transactionType").value(transactionResponse.getTransactionType().toString()));
    }
}
