package com.example.ecommerce.controllers;

import com.example.ecommerce.entities.Cart;
import com.example.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce.exceptions.UserNotExistsException;
import com.example.ecommerce.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Cart> getCart() throws UserNotExistsException {
        Cart cart = cartService.getCartByCurrentUser();
        return ResponseEntity.ok(cart);
    }


    @PostMapping("/add")
    public ResponseEntity<Cart> addItemToCart(
            @RequestParam int productId,
            @RequestParam int quantity) throws ProductNotFoundException, UserNotExistsException {

        Cart updatedCart = cartService.addItemToCart(productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Cart> removeItemFromCart(
            @RequestParam int productId) throws UserNotExistsException {

        Cart updatedCart = cartService.removeItemFromCart(productId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() throws UserNotExistsException {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}




