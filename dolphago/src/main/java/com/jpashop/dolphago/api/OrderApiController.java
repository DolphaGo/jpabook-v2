package com.jpashop.dolphago.api;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpashop.dolphago.domain.shop.Address;
import com.jpashop.dolphago.domain.shop.Order;
import com.jpashop.dolphago.domain.shop.OrderItem;
import com.jpashop.dolphago.domain.shop.OrderStatus;
import com.jpashop.dolphago.repository.OrderRepository;
import com.jpashop.dolphago.repository.OrderSearch;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    /**
     * 엔티티를 직접 노출시 (하면 안됨)
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            // 강제 초기화
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.forEach(o -> o.getItem().getName());
        }
        return all;
    }

    /**
     * DTO 로 노출하기
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        return orders.stream()
                     .map(OrderDto::new)
                     .collect(toList());
    }

    /**
     * 페치 조인으로 최적화하기 (한방 쿼리)
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        System.out.println("주문 개수 :" + orders.size());
        List<OrderDto> result = orders
                .stream()
                .map(OrderDto::new)
                .collect(toList());
        return result;
    }

    @Getter
    private class OrderDto {
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
            this.orderItems = order.getOrderItems()
                                   .stream()
                                   .map(OrderItemDto::new)
                                   .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto {
        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}
