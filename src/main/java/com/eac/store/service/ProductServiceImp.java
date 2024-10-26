package com.eac.store.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eac.store.model.Product;
import com.eac.store.repository.ProductRepository;

@Service
public class ProductServiceImp implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProduct(String keyword) {
        if (keyword != null) {
            return productRepository.search(keyword);
        } else
            return (List<Product>) productRepository.findAll();
    }

    public Product saveProduct(Product product) {
        return this.productRepository.save(product);
    }

    public Product getProductById(long id) {
        Optional<Product> optional = productRepository.findById(id);
        Product product = null;
        if (optional.isPresent()) {
            product = optional.get();
        } else {
            throw new RuntimeException("Employee not found for id ::" + id);
        }
        return product;
    }

    public void deleteProductById(long id) {
        this.productRepository.deleteById(id);
    }

    public Page<Product> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        // Validate pageNo to ensure it's not less than zero
        if (pageNo < 0) {
            pageNo = 0; // Or handle it according to your business logic
        }

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return this.productRepository.findAll(pageable);
    }
}