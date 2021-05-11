package com.jpashop.dolphago.service.query; // 핵심 비즈니스랑, 쿼리용 서비스를 구분하는 것이 좋음. 지금도 그렇게 맘에 드는 형태는 아님

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpashop.dolphago.domain.shop.Order;
import com.jpashop.dolphago.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        System.out.println("주문 개수 :" + orders.size());
        List<OrderDto> result = orders
                .stream()
                .map(OrderDto::new)
                .collect(toList());
        return result;
    }

}
