package com.web.demo.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.web.demo.exceptions.ResourceNotFoundException;
import com.web.demo.reader.JsonFileReader;
import com.web.demo.records.Customer;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final String FILE_NAME = "customers.json";

    private final JsonFileReader jsonFileReader;

    private List<Customer> customers;

    public CustomerServiceImpl(JsonFileReader jsonFileReader) {
        this.jsonFileReader = jsonFileReader;
    }

    @PostConstruct
    public void loadCustomers() {
        this.customers = jsonFileReader.readListFromFile(
                FILE_NAME,
                new TypeReference<>() {}
        );
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customers;
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customers.stream()
                .filter(c -> c.id().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + id));
    }
}

