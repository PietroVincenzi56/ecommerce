package com.example.ecommerce.services;

import com.example.ecommerce.entities.Order;
import com.example.ecommerce.entities.Product;
import com.example.ecommerce.entities.ProductOrder;
import com.example.ecommerce.entities.User;
import com.example.ecommerce.exceptions.NotEnoughQuantityException;
import com.example.ecommerce.exceptions.UserNotExistsException;
import com.example.ecommerce.exceptions.WrongDateException;
import com.example.ecommerce.repositories.OrderRepository;
import com.example.ecommerce.repositories.ProductOrderRepository;
import com.example.ecommerce.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private EntityManager entityManager;


    @Transactional
    public Order addOrder(Order order) throws NotEnoughQuantityException {
        Order savedOrder = orderRepository.save(order);
        for (ProductOrder p : savedOrder.getProductOrders()) { //da rivedere
            p.setOrder(savedOrder);

            Product product = p.getProduct();

            int remainingQuantity = product.getQuantity() - p.getQuantity();
            if (remainingQuantity < 0) {
                throw new NotEnoughQuantityException();
            }
            // Aggiorna quantità disponibile
            product.setQuantity(remainingQuantity);
            // Salva il legame prodotto-ordine
            productOrderRepository.save(p);
        }
        entityManager.refresh(savedOrder);
        return savedOrder;
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
