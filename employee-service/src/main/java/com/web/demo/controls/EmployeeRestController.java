package com.web.demo.controls;

import com.web.demo.records.Employee;
import com.web.demo.services.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("employee")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("list")
    public List<Employee> getAllCustomers() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Employee getCustomerById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }
}
