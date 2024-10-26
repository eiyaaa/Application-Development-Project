package com.eac.store.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

import com.eac.store.model.CartItem;
import com.eac.store.service.CartService;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class AdminCartController {

    private final CartService cartService;

    public AdminCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/CartAdmin/add")
    public String addToCart(@RequestParam("productId") long productId, @RequestParam("quantity") int quantity) {
    	cartService.addProductToCart(productId, quantity);
        return "redirect:/CartAdmin";
    }

    @GetMapping("/CartAdmin")
    public String viewCart(Model model, HttpSession session) {
        List<CartItem> cartItems = cartService.getCartItems();
        session.setAttribute("cartItems", cartItems);
        double totalPrice = cartItems.stream()
                                    .mapToDouble(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
                                    .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "CartAdmin";
    }
    
    @PostMapping("/CartAdmin/updateQuantity")
    public String updateQuantity(@RequestParam("productId") long productId, @RequestParam("action") String action) {
        CartItem cartItem = cartService.getCartItemByProductId(productId);
        if (cartItem != null) {
            if ("increase".equals(action)) {
                cartService.updateProductQuantity(productId, 0);
            } else if ("decrease".equals(action)) {
                cartService.updateProductQuantity(productId, -1);
            }
        }
        return "redirect:/CartAdmin";
    }

    @PostMapping("/CartAdmin/remove")
    public String removeFromCart(@RequestParam("productId") long productId) {
        cartService.removeProductFromCart(productId);
        return "redirect:/CartAdmin";    }
}