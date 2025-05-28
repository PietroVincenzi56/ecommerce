package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameContaining(String name);
    List<Product> findByCodeEAN(String codeEAN);
    List<Product> findByPrice(float price);
    boolean existsByCodeEAN(String codeEAN);

    @Query("SELECT p " +
            "FROM Product p " +
            "WHERE (p.name LIKE ?1 OR ?1 IS NULL) AND " +
            "      (p.description LIKE ?2 OR ?2 IS NULL)")
    List<Product> advancedSearch(String name, String description);

}
