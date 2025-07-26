package com.example.ecommerce.services;

import com.example.ecommerce.entities.Cart;
import com.example.ecommerce.entities.CartItem;
import com.example.ecommerce.entities.Product;
import com.example.ecommerce.entities.User;
import com.example.ecommerce.exceptions.CartItemNotFoundException;
import com.example.ecommerce.exceptions.ProductNotFoundException;
import com.example.ecommerce.exceptions.UserNotExistsException;
import com.example.ecommerce.repositories.CartItemRepository;
import com.example.ecommerce.repositories.CartRepository;
import com.example.ecommerce.repositories.ProductRepository;
import com.example.ecommerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    private User getCurrentUser() throws UserNotExistsException {
        return userService.syncUserWithKeycloak();

    }

    private Cart getCartByUser(User user) {
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    public Cart getCartByCurrentUser() throws UserNotExistsException {
        User user = getCurrentUser();
        Cart cart = getCartByUser(user);
        return cart;
    }

    public Cart addItemToCart(int productId, int quantity) throws ProductNotFoundException, UserNotExistsException {
        User user = getCurrentUser();
        Cart cart = getCartByUser(user);

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException();
        }

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return cartRepository.save(cart);
    }

    public Cart removeItemFromCart(int productId) throws UserNotExistsException, CartItemNotFoundException {
        User user = getCurrentUser();
        Cart cart = getCartByUser(user);

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst()
                .orElse(null);

        if (itemToRemove == null) {
            throw new CartItemNotFoundException();
        }

        cart.getItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);
        return cartRepository.save(cart);
    }

    public void clearCart() throws UserNotExistsException {
        User user = getCurrentUser();
        Cart cart = getCartByUser(user);

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public Cart getCart() throws UserNotExistsException {
        User user = getCurrentUser();
        return getCartByUser(user);
    }
}