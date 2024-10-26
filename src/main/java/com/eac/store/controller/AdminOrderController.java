package com.eac.store.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.eac.store.model.CartItem;
import com.eac.store.model.Order;
import com.eac.store.model.OrderDetail;
import com.eac.store.model.User;
import com.eac.store.repository.UserRepository;
import com.eac.store.service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = Logger.getLogger(AdminOrderController.class.getName());

    @GetMapping("/orders")
    public String viewOrders(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<Order> listOrders;
        if (keyword != null && !keyword.trim().isEmpty()) {
            listOrders = orderService.searchOrders(keyword);
        } else {
            listOrders = orderService.findAllOrders(); // This method already sorts by creationDate DESC
        }

        if (listOrders == null || listOrders.isEmpty()) {
            logger.warning("No orders found!");
        } else {
            logger.info("Orders found: " + listOrders.size());
        }

        model.addAttribute("listOrders", listOrders);
        return "orders";
    }
    
    @GetMapping("/order_details/{id}")
    public String viewAdminOrderDetails(@PathVariable("id") Long id, Model model) {
    	Order order = orderService.findOrderWithItemsById(id);
        if (order == null) {
            model.addAttribute("error", "Order not found");
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        return "order_details";
    }

    @GetMapping("/AdminOrder_form")
    public String showOrderForm(Model model, Principal principal, HttpSession session) {
    	User user = (User) session.getAttribute("user");
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");

        if (principal != null) {
            String username = principal.getName();
            user = userRepository.findByEmail(username);
        }

        String orderNumber = generateOrderNumber();
        Order order = new Order();
        order.setOrderNumber(orderNumber);

        order.setId(generateOrderId());
        order.setOrderNumber(orderNumber);
        order.setFirstName(user.getFirstName());
        order.setLastName(user.getLastName());
        order.setUserId(user.getId());

        double total = 0;
        if (cartItems != null) {
            total = cartItems.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
        }
        order.setTotal(total);
        session.setAttribute("order", order);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("total", total);

        return "AdminOrder_form";
    }
    
    @PostMapping("/AdminOrder_form/update")
    public String updateQuantities(@RequestParam("quantities") List<Integer> quantities, HttpSession session) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");

        if (cartItems != null && quantities != null && cartItems.size() == quantities.size()) {
            for (int i = 0; i < cartItems.size(); i++) {
                cartItems.get(i).setQuantity(quantities.get(i));
            }

            double total = cartItems.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
            Order order = (Order) session.getAttribute("order");
            if (order != null) {
                order.setTotal(total);
                session.setAttribute("order", order);
            }

            session.setAttribute("cartItems", cartItems);
        }

        return "redirect:/AdminOrder_form";
    }

    @PostMapping("/AdminOrder_summary")
    public String showOrderSummary(@RequestParam("userId") Integer userId, HttpSession session, Model model) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        User user = userRepository.findById(userId).orElse(null);
        Order order = (Order) session.getAttribute("order");

        model.addAttribute("user", user);
        model.addAttribute("order", order);
        model.addAttribute("cartItems", cartItems);
        return "AdminOrder_summary";
    }

    @PostMapping("/admin/order/save")
    public String saveOrder(HttpSession session, Model model) {
        Order order = (Order) session.getAttribute("order");
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");

        if (order != null) {
            order.setItems(cartItems);

            List<OrderDetail> orderDetails = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProduct(cartItem.getProduct());
                orderDetail.setProductName(cartItem.getProduct().getProductName());
                orderDetail.setQuantity(cartItem.getQuantity());
                orderDetail.setPrice(cartItem.getProduct().getPrice());
                orderDetail.setTotal(cartItem.getProduct().getPrice() * cartItem.getQuantity());
                orderDetail.setUserId(order.getUserId());

                orderDetails.add(orderDetail);
            }
            order.setOrderDetails(orderDetails);

            orderService.saveOrder(order);

            session.removeAttribute("cartItems");
            session.removeAttribute("order");

            model.addAttribute("order", order);
            model.addAttribute("cartItems", cartItems);
            return "AdminOrder_confirmation";
        } else {
            model.addAttribute("error", "Order not found");
            return "AdminOrder_summary";
        }
    }

    private String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "ORD-" + now.format(formatter);
    }

    private Long generateOrderId() {
        return System.currentTimeMillis();
    }
}