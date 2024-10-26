package com.eac.store.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.eac.store.FileUploadUtil;
import com.eac.store.model.Product;
import com.eac.store.service.ProductService;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        return findPaginated(1, "id", "asc", model);
    }

    @GetMapping("/showNewProductForm")
    public String showNewProductForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "Add_Product";
    }

    @PostMapping("/saveProduct")
    public RedirectView saveProduct(@ModelAttribute("product") Product product,
                                    @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            product.setPhotos(fileName);
            Product savedProduct = productService.saveProduct(product);

            String uploadDir = "product-photos/" + savedProduct.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            // If this is an update (product already has an ID)
            if (product.getId() > 0) {
                Product existingProduct = productService.getProductById(product.getId());
                product.setPhotos(existingProduct.getPhotos());
                productService.saveProduct(product);
            } else {
                // Save new product without image
                productService.saveProduct(product);
            }
        }

        return new RedirectView("/index", true);
    }

    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable("id") long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "Edit_product";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") long id) {
        productService.deleteProductById(id);
        return "redirect:/";
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable("pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {
        int pageSize = 3;
        Page<Product> page = productService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Product> listProduct = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listProduct", listProduct);
        return "index";
    }
}