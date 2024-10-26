package com.eac.store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eac.store.model.Product;
import com.eac.store.model.User;
import com.eac.store.service.ProductService;
import com.eac.store.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService service;
    private ProductService productService;
    
    public UserController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping("/registration")
    public String showUserPage(Model model) {
        model.addAttribute("user", new User());
        return "registration-form";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }
    
    @GetMapping("/index")
    public String showProducts(Model model, @Param("keyword") String keyword) {
        List<Product> listProduct = productService.getAllProduct(keyword);
        model.addAttribute("listProduct", listProduct);
        model.addAttribute("keyword", keyword);
        return "index";
    }

    @PostMapping("/login/validate")
    public String validateLogin(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                RedirectAttributes redi,  HttpSession session) {
        User user = service.findByEmail(email);
        if (user == null) {
            redi.addFlashAttribute("message", "Credentials not found...");
            return "redirect:/login";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            redi.addFlashAttribute("message", "Invalid login credentials...");
            return "redirect:/login";
        }

        session.setAttribute("user", user);
        if (user.getRole().equals("ADMIN")) {
            return "redirect:/adminHome";
        } else {
        	System.out.println(user.getId());
            return "redirect:/home";
        }
    }

    @PostMapping("/registration/save")
    public String saveUserForm(@ModelAttribute("user") User user,
                               RedirectAttributes redi) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setRole(user.getRole().toUpperCase()); // Make sure role is in uppercase
        redi.addFlashAttribute("message", "Credentials have been saved...");
        service.save(user);
        return "redirect:/login";
    }
}