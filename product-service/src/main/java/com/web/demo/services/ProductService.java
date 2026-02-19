package com.web.demo.services;

import com.web.demo.records.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    Product getProductById(Long id);
}
