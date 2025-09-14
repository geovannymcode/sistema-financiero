package com.geovannycode.domain.port.in;

import com.geovannycode.application.dto.CustomerDTO;

import java.util.List;
import java.util.Optional;

public interface CustomerUseCase {

    CustomerDTO createCustomer(CustomerDTO customerDTO);

    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);

    void deleteCustomer(Long id);

    Optional<CustomerDTO> findCustomerById(Long id);

    List<CustomerDTO> listCustomers();
}