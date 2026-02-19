package com.web.demo.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.web.demo.exceptions.ResourceNotFoundException;
import com.web.demo.reader.JsonFileReader;
import com.web.demo.records.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    private static final String FILE_NAME = "products.json";

    private final JsonFileReader jsonFileReader;

    private List<Product> products;

    public ProductServiceImpl(JsonFileReader jsonFileReader) {
        this.jsonFileReader = jsonFileReader;
    }

    @PostConstruct
    public void loadProducts() {
        this.products = jsonFileReader.readListFromFile(
                FILE_NAME,
                new TypeReference<>() {}
        );
    }

    @Override
    public List<Product> getAllProducts() {
        return products;
    }

    @Override
    public Product getProductById(Long id) {
        return products.stream()
                .filter(c -> c.id().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + id));
    }
}
