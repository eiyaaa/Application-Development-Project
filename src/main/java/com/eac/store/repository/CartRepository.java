package com.eac.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.eac.store.model.CartItem;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {
	CartItem findByProductId(Long productId);
}