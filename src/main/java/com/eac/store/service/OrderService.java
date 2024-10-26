package com.eac.store.service;

import java.util.List;
import com.eac.store.model.Order;

public interface OrderService {
    Order saveOrder(Order order);
    List<Order> getAllOrders();
    Order getOrderById(long id);
	List<Order> searchOrders(String keyword);
	List<Order> findAllOrders();
	Order findOrderById(Long id);
	Order findOrderWithItemsById(Long id);
}