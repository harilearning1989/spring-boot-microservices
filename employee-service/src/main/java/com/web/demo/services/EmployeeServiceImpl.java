package com.web.demo.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.web.demo.exceptions.ResourceNotFoundException;
import com.web.demo.reader.JsonFileReader;
import com.web.demo.records.Employee;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final String FILE_NAME = "EMPLOYEE_DATA.json";

    private final JsonFileReader jsonFileReader;

    private List<Employee> employees;

    public EmployeeServiceImpl(JsonFileReader jsonFileReader) {
        this.jsonFileReader = jsonFileReader;
    }

    @PostConstruct
    public void loadCustomers() {
        this.employees = jsonFileReader.readListFromFile(
                FILE_NAME,
                new TypeReference<>() {}
        );
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employees;
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employees.stream()
                .filter(c -> c.id().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + id));
    }
}
