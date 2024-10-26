package com.eac.store.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eac.store.model.CartItem;
import com.eac.store.model.Product;
import com.eac.store.repository.CartItemRepository;
import com.eac.store.repository.ProductRepository;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public void addProductToCart(long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));
        CartItem cartItem = cartItemRepository.findByProductId(productId);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }
        cartItemRepository.save(cartItem);
    }

    public List<CartItem> getCartItems() {
        return cartItemRepository.findAll();
    }

    public void removeProductFromCart(long productId) {
        CartItem cartItem = cartItemRepository.findByProductId(productId);
        if (cartItem != null) {
            cartItemRepository.delete(cartItem);
        }
    }

    public void updateProductQuantity(long productId, int delta) {
        CartItem cartItem = cartItemRepository.findByProductId(productId);
        if (cartItem != null) {
            int newQuantity = cartItem.getQuantity() + delta;
            if (newQuantity >= 0) {
                cartItem.setQuantity(newQuantity);
                cartItemRepository.save(cartItem);
            }
        }
    }

    public CartItem getCartItemByProductId(long productId) {
        return cartItemRepository.findByProductId(productId);
    }
}
