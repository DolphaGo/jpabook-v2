package com.jpashop.dolphago.api;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpashop.dolphago.domain.shop.Order;
import com.jpashop.dolphago.domain.shop.OrderItem;
import com.jpashop.dolphago.repository.OrderRepository;
import com.jpashop.dolphago.repository.OrderSearch;
import com.jpashop.dolphago.repository.order.query.OrderFlatDto;
import com.jpashop.dolphago.repository.order.query.OrderItemQueryDto;
import com.jpashop.dolphago.repository.order.query.OrderQueryDto;
import com.jpashop.dolphago.repository.order.query.OrderQueryRepository;
import com.jpashop.dolphago.service.query.OrderDto;
import com.jpashop.dolphago.service.query.OrderQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final OrderQueryService orderQueryService;

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
        return orderQueryService.ordersV3(); // OSIV로 Query / Command를 분리
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

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    /**
     * @implNote
     * 장점
     * Query: 1번 단점
     *
     * 단점
     * 쿼리는 한번이지만 조인으로 인해 DB에서 애플리케이션에 전달하는 데이터에 중복 데이터가 추가되므로 상황에 따라 V5 보다 더 느릴 수 도 있다.
     * 애플리케이션에서 추가 작업이 크다.
     * 페이징 불가능
     */
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats.stream()
                    .collect(
                            groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                                       mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList()))
                    ).entrySet().stream()
                    .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(),
                                                e.getValue()))
                    .collect(toList());
    }

}
