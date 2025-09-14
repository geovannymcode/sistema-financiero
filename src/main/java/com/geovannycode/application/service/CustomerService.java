package com.geovannycode.application.service;

import com.geovannycode.application.dto.CustomerDTO;
import com.geovannycode.application.mapper.CustomerMapper;
import com.geovannycode.domain.exception.CustomerHasAccountsException;
import com.geovannycode.domain.exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService implements CustomerUseCase {

    private final CustomerPort customerPort;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        if (isUnderage(customer.getBirthDate())) {
            throw new UnderageCustomerException("Cannot register an underage customer");
        }

        if (customerPort.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("A customer with this email already exists");
        }

        Customer savedCustomer = customerPort.saveCustomer(customer);
        return customerMapper.toDTO(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        Customer customerData = customerMapper.toEntity(customerDTO);

        if (isUnderage(customerData.getBirthDate())) {
            throw new UnderageCustomerException("Cannot update to an underage customer");
        }

        existingCustomer.updateInfo(
                customerData.getIdentificationType(),
                customerData.getIdentificationNumber(),
                customerData.getFirstName(),
                customerData.getLastName(),
                customerData.getEmail(),
                customerData.getBirthDate()
        );
        Customer updatedCustomer = customerPort.saveCustomer(existingCustomer);
        return customerMapper.toDTO(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        if (customerPort.customerHasAccounts(id)) {
            throw new CustomerHasAccountsException("Cannot delete a customer with linked accounts");
        }
        customerPort.deleteCustomer(id);
    }

    @Override
    @Transactional
    public Optional<CustomerDTO> findCustomerById(Long id) {
        return customerPort.findById(id)
                .map(customerMapper::toDTO);
    }

    @Override
    @Transactional
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerPort.findAll();
        return customers.stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }

    private boolean isUnderage(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() < 18;
    }
}
