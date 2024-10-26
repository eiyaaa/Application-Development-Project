package com.eac.store.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.eac.store.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Integer integer);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product")
    List<Order> findAll(Sort sort);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE " +
           "LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(o.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(o.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Order> searchByKeyword(String keyword);
	Optional<Order> findOrderWithItemsById(Long id);
}