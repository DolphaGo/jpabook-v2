package com.jpashop.dolphago.repository.order.query;

import java.time.LocalDateTime;
import java.util.List;

import com.jpashop.dolphago.domain.shop.Address;
import com.jpashop.dolphago.domain.shop.OrderStatus;

import lombok.Data;

@Data
public class OrderQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems;

    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
