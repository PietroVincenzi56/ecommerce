package com.example.ecommerce.controllers;

import com.example.ecommerce.entities.Order;
import com.example.ecommerce.entities.Product;
import com.example.ecommerce.entities.ProductOrder;
import com.example.ecommerce.entities.User;
import com.example.ecommerce.exceptions.*;
import com.example.ecommerce.other.Message;
import com.example.ecommerce.services.OrderService;
import com.example.ecommerce.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping
    public ResponseEntity addOrders(@RequestBody Order order) {
        try {
            orderService.addOrder(order) ;
        } catch (NotEnoughQuantityException e) {
            return new ResponseEntity<>(new Message("Non ne abbiamo abbastanza"), HttpStatus.BAD_REQUEST);
        } catch (PriceChangedException e){
            return new ResponseEntity<>(new Message("Il prodotto che stavi ordinando è stato modificato"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(order,  HttpStatus.OK);
    }

    @GetMapping
    public List<Order> getOrders() {
        return orderService.getAllOrders();
    }



    @GetMapping("/{userId}")
    public List<Order> getUserOrders(@PathVariable("userId") int userId) {
        try {
            return orderService.getOrdersByUserId(userId);
        } catch (UserNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found", e);
        }
    }

    @GetMapping("/{user}")
    public List<Order> getUserOrdersInPeriod(@RequestBody User user) {
        try {
            return orderService.getOrdersByUser(user);
        } catch (UserNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found", e);
        }
    }

    @GetMapping("/{user}/{startDate}/{endDate}")
    public ResponseEntity getPurchasesInPeriod(@PathVariable("user") User user, @PathVariable("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date start, @PathVariable("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date end) {
        try {
            List<Order> result = orderService.getOrdersByUserInPeriod(user, start, end);
            if (result.isEmpty()) {
                return new ResponseEntity<>(new Message("No results!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UserNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found", e);
        } catch (WrongDateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong date", e);
        }
    }

}
