package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByName(String name);
    List<User> findBySurname(String surname);
    User findByEmail(String email);
    List<User> findByTelephoneNumber(String telephoneNumber);
    List<User> findByNameAndSurname(String name, String surname);
    List<User> findByCode(String code);
    boolean existsByEmail(String email);

}
