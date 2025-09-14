package com.geovannycode.application.service;

import com.geovannycode.domain.exception.CustomerHasAccountsException;
import com.geovannycode.domain.exception.UnderageCustomerException;
import com.geovannycode.domain.model.Customer;
import com.geovannycode.domain.port.in.CustomerUseCase;
import com.geovannycode.domain.port.out.CustomerPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService implements CustomerUseCase {

    private final CustomerPort customerPort;

    @Override
    @Transactional
    public Customer createCustomer(Customer customer) {
        if (isUnderage(customer.getBirthDate())) {
            throw new UnderageCustomerException("Cannot register an underage customer");
        }

        if (customerPort.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("A customer with this email already exists");
        }

        return customerPort.saveCustomer(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, Customer customer) {
        Customer existingCustomer = customerPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));

        if (isUnderage(customer.getBirthDate())) {
            throw new UnderageCustomerException("Cannot update to an underage customer");
        }

        existingCustomer.updateInfo(
                customer.getIdentificationType(),
                customer.getIdentificationNumber(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getBirthDate()
        );

        return customerPort.saveCustomer(existingCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));

        if (customerPort.customerHasAccounts(id)) {
            throw new CustomerHasAccountsException("Cannot delete a customer with linked accounts");
        }

        customerPort.deleteCustomer(id);
    }

    @Override
    @Transactional
    public Optional<Customer> findCustomerById(Long id) {
        return customerPort.findById(id);
    }

    @Override
    @Transactional
    public List<Customer> listCustomers() {
        return customerPort.findAll();
    }

    private boolean isUnderage(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() < 18;
    }
}
