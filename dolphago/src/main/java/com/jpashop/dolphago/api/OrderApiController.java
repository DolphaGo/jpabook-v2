package com.jpashop.dolphago.api;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * 단점 :
     * 실제로 데이터 베이스로 가면 데이터 중복이 많음(정규화가 안되어있음)
     * DB에서 Application으로 많은 데이터가 전송이 되고 커팅이 됨
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

    /**
     * 페이징 + 페치 조인 + 성능 최적화까지
     * DB에 가보면 최적화되어서 정규화되어 있는 것을 확인할 수 있음
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        return orders.stream()
                     .map(OrderDto::new)  //default_batch_fetch_size 설정으로 1:N:M을 1:1:1로 최적화
                     .collect(toList());
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
            this.orderItems = order.getOrderItems().stream()
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
