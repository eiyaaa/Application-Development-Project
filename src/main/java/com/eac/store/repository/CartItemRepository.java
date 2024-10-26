package com.eac.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.eac.store.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	CartItem findByProductId(long productId);
}