package com.geovannycode.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geovannycode.application.dto.AccountDTO;
import com.geovannycode.domain.model.enums.AccountStatus;
import com.geovannycode.domain.port.in.AccountUseCase;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountUseCase accountUseCase;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AccountDTO accountRequest;
    private AccountDTO accountResponse;

    @BeforeEach
    void setUp() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        try (InputStream requestStream = new ClassPathResource("json/account-request.json").getInputStream();
             InputStream responseStream = new ClassPathResource("json/account-response.json").getInputStream()) {

            accountRequest = objectMapper.readValue(requestStream, AccountDTO.class);
            accountResponse = objectMapper.readValue(responseStream, AccountDTO.class);
        }
    }

    @Test
    void createAccount_Success() throws Exception {
        // Given
        when(accountUseCase.createAccount(any(AccountDTO.class), anyLong())).thenReturn(accountResponse);

        // When & Then
        mockMvc.perform(post("/api/accounts/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(accountResponse.getId()))
                .andExpect(jsonPath("$.accountNumber").value(accountResponse.getAccountNumber()))
                .andExpect(jsonPath("$.accountType").value(accountResponse.getAccountType().toString()));
    }

    @Test
    void changeStatus_Success() throws Exception {
        // Given
        AccountDTO updatedAccount = accountResponse.toBuilder()
                .status(AccountStatus.INACTIVE)
                .build();

        when(accountUseCase.changeStatus(anyLong(), any(AccountStatus.class))).thenReturn(updatedAccount);

        // When & Then
        mockMvc.perform(patch("/api/accounts/1/status")
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedAccount.getId()))
                .andExpect(jsonPath("$.status").value(updatedAccount.getStatus().toString()));
    }

    @Test
    void cancelAccount_Success() throws Exception {
        // Given
        doNothing().when(accountUseCase).cancelAccount(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAccountById_Success() throws Exception {
        // Given
        when(accountUseCase.findAccountById(anyLong())).thenReturn(Optional.of(accountResponse));

        // When & Then
        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountResponse.getId()))
                .andExpect(jsonPath("$.accountNumber").value(accountResponse.getAccountNumber()));
    }

    @Test
    void getAccountById_NotFound() throws Exception {
        // Given
        when(accountUseCase.findAccountById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAccountByNumber_Success() throws Exception {
        // Given
        when(accountUseCase.findAccountByNumber(anyString())).thenReturn(Optional.of(accountResponse));

        // When & Then
        mockMvc.perform(get("/api/accounts/number/5312345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountResponse.getId()))
                .andExpect(jsonPath("$.accountNumber").value(accountResponse.getAccountNumber()));
    }

    @Test
    void getAccountsByCustomerId_Success() throws Exception {
        // Given
        when(accountUseCase.getAccountsByCustomerId(anyLong())).thenReturn(Arrays.asList(accountResponse));

        // When & Then
        mockMvc.perform(get("/api/accounts/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(accountResponse.getId()))
                .andExpect(jsonPath("$[0].accountNumber").value(accountResponse.getAccountNumber()));
    }
}
