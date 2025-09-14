package com.geovannycode.domain.port.out;

import com.geovannycode.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerPort {

    Customer saveCustomer(Customer customer);
    Optional<Customer> findById(Long id);
    void deleteCustomer(Long id);
    List<Customer> findAll();
    boolean customerHasAccounts(Long id);
    boolean existsByEmail(String email);
    Optional<Customer> findByIdentificationNumber(String identificationNumber);
}
