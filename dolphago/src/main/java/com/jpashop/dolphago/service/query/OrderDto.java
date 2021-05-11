package com.jpashop.dolphago.service.query;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;

import com.jpashop.dolphago.domain.shop.Address;
import com.jpashop.dolphago.domain.shop.Order;
import com.jpashop.dolphago.domain.shop.OrderStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OrderDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
        this.orderId = order.getId();
        this.name = order.getMember().getName();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.address = order.getDelivery().getAddress();
//            order.getOrderItems().forEach(i -> i.getItem().getName());  // orderItem은 프록시 엔티티이기 때문에 json에는 Null로 반환됨. 따라서 강제 초기화로 해결한다.
        this.orderItems = order.getOrderItems().stream()
                               .map(OrderItemDto::new)
                               .collect(toList());
    }
}

