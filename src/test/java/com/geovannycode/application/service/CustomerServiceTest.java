package com.geovannycode.application.service;

import com.geovannycode.application.dto.CustomerDTO;
import com.geovannycode.application.mapper.CustomerMapper;
import com.geovannycode.domain.exception.CustomerHasAccountsException;
import com.geovannycode.domain.exception.ResourceNotFoundException;
import com.geovannycode.domain.exception.UnderageCustomerException;
import com.geovannycode.domain.model.Customer;
import com.geovannycode.domain.port.out.CustomerPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerPort customerPort;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        customer = Customer.builder()
                .id(1L)
                .identificationType("CC")
                .identificationNumber("123456789")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .birthDate(LocalDate.now().minusYears(25))
                .build();

        customerDTO = CustomerDTO.builder()
                .id(1L)
                .identificationType("CC")
                .identificationNumber("123456789")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .birthDate(LocalDate.now().minusYears(25))
                .build();
    }

    @Test
    void createCustomer_Success() {
        // Given
        when(customerMapper.toEntity(any(CustomerDTO.class))).thenReturn(customer);
        when(customerPort.saveCustomer(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toDTO(any(Customer.class))).thenReturn(customerDTO);

        // When
        CustomerDTO result = customerService.createCustomer(customerDTO);

        // Then
        assertNotNull(result);
        assertEquals(customerDTO.getId(), result.getId());
        verify(customerPort).saveCustomer(any(Customer.class));
    }

    @Test
    void createCustomer_UnderageCustomer_ThrowsException() {
        // Given
        Customer underageCustomer = Customer.builder()
                .birthDate(LocalDate.now().minusYears(15))
                .build();

        when(customerMapper.toEntity(any(CustomerDTO.class))).thenReturn(underageCustomer);

        // When & Then
        assertThrows(UnderageCustomerException.class, () ->
                customerService.createCustomer(customerDTO)
        );
    }

    @Test
    void updateCustomer_Success() {
        // Given
        when(customerPort.findById(anyLong())).thenReturn(Optional.of(customer));
        when(customerMapper.toEntity(any(CustomerDTO.class))).thenReturn(customer);
        when(customerPort.saveCustomer(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toDTO(any(Customer.class))).thenReturn(customerDTO);

        // When
        CustomerDTO result = customerService.updateCustomer(1L, customerDTO);

        // Then
        assertNotNull(result);
        assertEquals(customerDTO.getId(), result.getId());
        verify(customerPort).saveCustomer(any(Customer.class));
    }

    @Test
    void updateCustomer_CustomerNotFound_ThrowsException() {
        // Given
        when(customerPort.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
                customerService.updateCustomer(1L, customerDTO)
        );
    }

    @Test
    void deleteCustomer_Success() {
        // Given
        when(customerPort.findById(anyLong())).thenReturn(Optional.of(customer));
        when(customerPort.customerHasAccounts(anyLong())).thenReturn(false);

        // When
        customerService.deleteCustomer(1L);

        // Then
        verify(customerPort).deleteCustomer(1L);
    }

    @Test
    void deleteCustomer_CustomerHasAccounts_ThrowsException() {
        // Given
        when(customerPort.findById(anyLong())).thenReturn(Optional.of(customer));
        when(customerPort.customerHasAccounts(anyLong())).thenReturn(true);

        // When & Then
        assertThrows(CustomerHasAccountsException.class, () ->
                customerService.deleteCustomer(1L)
        );
    }

    @Test
    void findCustomerById_Success() {
        // Given
        when(customerPort.findById(anyLong())).thenReturn(Optional.of(customer));
        when(customerMapper.toDTO(any(Customer.class))).thenReturn(customerDTO);

        // When
        Optional<CustomerDTO> result = customerService.findCustomerById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(customerDTO.getId(), result.get().getId());
    }

    @Test
    void listCustomers_Success() {
        // Given
        List<Customer> customers = Arrays.asList(customer);
        when(customerPort.findAll()).thenReturn(customers);
        when(customerMapper.toDTO(any(Customer.class))).thenReturn(customerDTO);

        // When
        List<CustomerDTO> result = customerService.listCustomers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customerDTO.getId(), result.get(0).getId());
    }
}
