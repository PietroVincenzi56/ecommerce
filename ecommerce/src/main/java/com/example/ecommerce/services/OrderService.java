package com.example.ecommerce.services;

import com.example.ecommerce.entities.Order;
import com.example.ecommerce.entities.Product;
import com.example.ecommerce.entities.ProductOrder;
import com.example.ecommerce.entities.User;
import com.example.ecommerce.exceptions.NotEnoughQuantityException;
import com.example.ecommerce.exceptions.PriceChangedException;
import com.example.ecommerce.exceptions.UserNotExistsException;
import com.example.ecommerce.exceptions.WrongDateException;
import com.example.ecommerce.repositories.OrderRepository;
import com.example.ecommerce.repositories.ProductOrderRepository;
import com.example.ecommerce.repositories.ProductRepository;
import com.example.ecommerce.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public Order addOrder(Order order) throws NotEnoughQuantityException, PriceChangedException {
        BigDecimal total = BigDecimal.ZERO;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userEmail = jwt.getClaim("email");
        User user= userRepository.findByEmail(userEmail);

        for (ProductOrder p : order.getProductOrders()) {
            p.setOrder(order);

            Product product = productRepository.findById(p.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getVersion()!=(p.getProduct().getVersion())) {
                throw new PriceChangedException();
            }

            int remainingQuantity = product.getQuantity() - p.getQuantity();
            if (remainingQuantity < 0) {
                throw new NotEnoughQuantityException();
            }

            product.setQuantity(remainingQuantity);

            // Salva il prezzo corrente del prodotto nel ProductOrder
            BigDecimal currentPrice = BigDecimal.valueOf(product.getPrice());
            p.setPriceAtPurchase(currentPrice);

            total = total.add(currentPrice.multiply(BigDecimal.valueOf(p.getQuantity())));

            p.setProduct(product);
            productOrderRepository.save(p);
        }

        // Verifica se l'utente ha credito sufficiente
        if (user.getBalance().compareTo(total) < 0) {
            throw new RuntimeException("Credito insufficiente");
        }

        // Scala il saldo
        user.setBalance(user.getBalance().subtract(total));
        userRepository.save(user);

        try {
            Order savedOrder = orderRepository.save(order);
            return savedOrder;
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException("Conflitto di aggiornamento, riprova.");
        }
    }



    @Transactional(readOnly = true)
    public List<Order> getPurchasesByUser(User user) throws UserNotExistsException {
        if ( !userRepository.existsById(user.getId()) ) { //il metodo è default di jpa
            throw new UserNotExistsException();
        }
        return orderRepository.findByBuyer(user);
    }

    @Transactional(readOnly = true)
    public List<Order> getPurchasesByUserInPeriod(User user, Date startDate, Date endDate) throws UserNotExistsException, WrongDateException{
        if ( !userRepository.existsById(user.getId()) ) {
            throw new UserNotExistsException();
        }
        if ( startDate.compareTo(endDate) >= 0 ) {
            throw new WrongDateException();
        }
        return orderRepository.findByBuyerInPeriod(startDate, endDate, user);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllPurchases() {
        return orderRepository.findAll();
    }


}
