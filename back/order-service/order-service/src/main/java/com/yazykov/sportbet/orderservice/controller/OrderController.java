package com.yazykov.sportbet.orderservice.controller;

import com.yazykov.sportbet.orderservice.dto.OrderDetailDto;
import com.yazykov.sportbet.orderservice.exception.OrderNotFoundException;
import com.yazykov.sportbet.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders/customer/{customerId}")
    public ResponseEntity<?> getCustomerOrders(@PathVariable("customerId") Long customerId){
        return ResponseEntity.ok(orderService.getAllOrdersByCustomerId(customerId));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable("orderId") String orderId)
            throws OrderNotFoundException {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody OrderDetailDto orderDetailDto){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(orderService.createNewOrder(orderDetailDto));
    }
}
