package com.example.ecommerce.controllers;

import com.example.ecommerce.repositories.ProductRepository;
import com.example.ecommerce.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;






}
