package com.eac.store.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.eac.store.model.Product;

public interface ProductService {
	Product saveProduct (Product product); 
	Product getProductById(long id);
	void deleteProductById(long id);
	List<Product> getAllProduct (String keyword);
	//code writen below for pagination
	Page<Product> findPaginated (int pageNo, int pageSize, String sortField, String sortDir);
}