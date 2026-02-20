package com.web.demo.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Employee(

        @JsonProperty("ID")
        Long id,

        @JsonProperty("EMP_ID")
        int empId,

        @JsonProperty("EMP_NAME")
        String empName,

        @JsonProperty("FATHER_NAME")
        String fatherName,

        @JsonProperty("GENDER")
        String gender,

        @JsonProperty("CATEGORY")
        String category,

        @JsonProperty("MANAGER_ID")
        int managerId,

        @JsonProperty("DESIGNATION")
        String designation,

        @JsonProperty("SALARY")
        double salary

) {}