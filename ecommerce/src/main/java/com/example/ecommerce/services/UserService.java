package com.example.ecommerce.services;

import com.example.ecommerce.entities.User;
import com.example.ecommerce.exceptions.MailUserAlreadyExistsException;
import com.example.ecommerce.exceptions.UserNotExistsException;
import com.example.ecommerce.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public User registerUser(User user) throws MailUserAlreadyExistsException {
        if ( userRepository.existsByEmail(user.getEmail()) ) {
            throw new MailUserAlreadyExistsException();
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public User syncUserWithKeycloak() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String email = jwt.getClaim("email");
        String name = jwt.getClaim("given_name"); // o "name"
        String surname = jwt.getClaim("family_name");
        String phone = jwt.getClaim("phone_number");
        String address = jwt.getClaim("address"); // solo se custom field mappato

        // Cerca utente nel DB
        User user = userRepository.findByEmail(email);

        if (user == null) {
            // Nuovo utente
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setSurname(surname);
            user.setTelephoneNumber(phone);
            user.setAddress(address);
            user.setBalance(BigDecimal.ZERO);
            user = userRepository.save(user);
        } else {
            // Aggiorna dati se null
            if (user.getName() == null) user.setName(name);
            if (user.getSurname() == null) user.setSurname(surname);
            if (user.getTelephoneNumber() == null) user.setTelephoneNumber(phone);
            if (user.getAddress() == null) user.setAddress(address);
            user = userRepository.save(user);
        }

        return user;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public User rechargeUserBalance(int userId, BigDecimal amount) throws UserNotExistsException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotExistsException());

        user.setBalance(user.getBalance().add(amount));
        return userRepository.save(user);
    }


}
