package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.Cart;
import com.example.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Cart findByUser(User user);
    Optional<Cart> findByUserId(int userId);
    boolean existsByUserId(int userId);

}
