package com.eac.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eac.store.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{ 
	//for search product
	@Query("SELECT product FROM Product product WHERE CONCAT(product.productName) LIKE %?1%")
	public List<Product> search(String keyword);
	public Product findByproductName(String productName);
}