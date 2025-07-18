package com.example.ecommerce.services;

import com.example.ecommerce.entities.Product;
import com.example.ecommerce.exceptions.BarCodeAlreadyExistException;
import com.example.ecommerce.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = false)
    public void addProduct(Product product) throws BarCodeAlreadyExistException {
        if ( product.getCodeEAN() != null && productRepository.existsByCodeEAN(product.getCodeEAN()) ) {
            throw new BarCodeAlreadyExistException();
        }
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<Product> getAllProducts(int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Product> pagedResult = productRepository.findAll(paging);
        if ( pagedResult.hasContent() ) {
            return pagedResult.getContent();
        }
        else {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCode(String codeEAN) {
        return productRepository.findByCodeEAN(codeEAN);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByPrice(float price) {
        return productRepository.findByPrice(price);
    }

    @Transactional(readOnly = true)
    public Optional<Product> getProductsById(int id) {
        return productRepository.findById(id);
    }

    //Non serve
    @Transactional(readOnly = true)
    public List<Product> getProductsByVersion(int id, int version) {
        return productRepository.findByIdAndVersion(id,version);
    }

    @Transactional(readOnly = false)
    public void deleteProduct(int productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Prodotto non trovato con ID: " + productId);
        }
        productRepository.deleteById(productId);
    }

    @Transactional(readOnly = false)
    public Product updateProduct(int productId, Product updatedProduct) {
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato con ID: " + productId));

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setQuantity(updatedProduct.getQuantity());
        existing.setCodeEAN(updatedProduct.getCodeEAN());

        return productRepository.save(existing);
    }

    public boolean existsById(int id) {
        return productRepository.existsById(id);
    }


}
