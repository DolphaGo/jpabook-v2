package com.jpashop.dolphago.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpashop.dolphago.domain.Order;
import com.jpashop.dolphago.repository.OrderRepository;
import com.jpashop.dolphago.repository.OrderSearch;

import lombok.RequiredArgsConstructor;

/**
 * xToOne
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all;
    }
}
