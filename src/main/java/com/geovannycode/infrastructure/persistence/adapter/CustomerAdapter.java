package com.geovannycode.infrastructure.persistence.adapter;

import com.geovannycode.domain.model.Customer;
import com.geovannycode.domain.port.out.CustomerPort;
import com.geovannycode.infrastructure.persistence.AccountRepository;
import com.geovannycode.infrastructure.persistence.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerAdapter implements CustomerPort {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public boolean customerHasAccounts(Long id) {
        return !accountRepository.findByCustomerId(id).isEmpty();
    }

    @Override
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Override
    public Optional<Customer> findByIdentificationNumber(String identificationNumber) {
        return customerRepository.findByIdentificationNumber(identificationNumber);
    }
}
