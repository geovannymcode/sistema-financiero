package com.geovannycode.domain.port.in;

import com.geovannycode.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerUseCase {

    Customer createCustomer(Customer customer);
    Customer updateCustomer(Long id, Customer customer);
    void deleteCustomer(Long id);
    Optional<Customer> findCustomerById(Long id);
    List<Customer> listCustomers();
}
