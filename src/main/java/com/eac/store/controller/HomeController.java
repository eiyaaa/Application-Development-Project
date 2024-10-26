package com.eac.store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eac.store.model.Product;
import com.eac.store.service.ProductService;

@Controller
public class HomeController {
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/home")
    public String showHomePage(Model model, @Param("keyword") String keyword) {
        List<Product> listProduct = productService.getAllProduct(keyword);
        model.addAttribute("listProduct", listProduct);
        model.addAttribute("keyword", keyword);
        return "home";
    }
	@GetMapping("/adminHome")
    public String showAdminHomePage(Model model, @Param("keyword") String keyword) {
        List<Product> listProduct = productService.getAllProduct(keyword);
        model.addAttribute("listProduct", listProduct);
        model.addAttribute("keyword", keyword);
        return "adminHome";
    }
}