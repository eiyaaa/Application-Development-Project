package com.eac.store.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eac.store.model.Order;
import com.eac.store.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
    private OrderRepository orderRepository;

    @Override
    public Order findOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
    
    public Order findOrderWithItemsById(Long id) {
        return orderRepository.findOrderWithItemsById(id).orElse(null);
    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(long id) {
        return orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + id));
    }

    @Override
    public List<Order> searchOrders(String keyword) {
        return orderRepository.searchByKeyword(keyword);
    }

    @Override
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }
}
