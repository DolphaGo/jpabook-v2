package com.jpashop.dolphago.api;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpashop.dolphago.domain.shop.Address;
import com.jpashop.dolphago.domain.shop.Order;
import com.jpashop.dolphago.domain.shop.OrderStatus;
import com.jpashop.dolphago.repository.OrderRepository;
import com.jpashop.dolphago.repository.OrderSearch;

import lombok.Data;
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
    public List<Order> ordersV1() {
        final List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all;
    }

    // 엔티티가 아니라 절대적이로 DTO로 바꾸어서 리턴할 것
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        // order가 2개
        // 실행 쿼리 수 = 1 + N + N 번
        // 1 + N 문제 ( 1 : Orders를 가져옴. N(=2)개 가져옴, 그리고 각각 2번씩(Member, Delivery) = 총 5번의 쿼리가 날아감)
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                                            .map(SimpleOrderDto::new)
                                            .collect(toList());
        return result;

    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
}
