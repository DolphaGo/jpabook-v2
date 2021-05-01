package com.jpashop.dolphago.api;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpashop.dolphago.domain.shop.Address;
import com.jpashop.dolphago.domain.shop.Order;
import com.jpashop.dolphago.domain.shop.OrderStatus;
import com.jpashop.dolphago.repository.OrderRepository;
import com.jpashop.dolphago.repository.OrderSearch;
import com.jpashop.dolphago.repository.order.simplequery.OrderSimpleQueryDto;
import com.jpashop.dolphago.repository.order.simplequery.OrderSimpleQueryRepository;

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
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

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

    //FetchJoin으로 1+N을 방지해보자

    /**
     *     select
     *         order0_.order_id as order_id1_6_0_,
     *         member1_.member_id as member_i1_4_1_,
     *         delivery2_.id as id1_2_2_,
     *         order0_.delivery_id as delivery4_6_0_,
     *         order0_.member_id as member_i5_6_0_,
     *         order0_.order_date as order_da2_6_0_,
     *         order0_.status as status3_6_0_,
     *         member1_.city as city2_4_1_,
     *         member1_.street as street3_4_1_,
     *         member1_.zipcode as zipcode4_4_1_,
     *         member1_.name as name5_4_1_,
     *         delivery2_.city as city2_2_2_,
     *         delivery2_.street as street3_2_2_,
     *         delivery2_.zipcode as zipcode4_2_2_,
     *         delivery2_.status as status5_2_2_
     *     from
     *         orders order0_
     *     inner join
     *         member member1_
     *             on order0_.member_id=member1_.member_id
     *     inner join
     *         delivery delivery2_
     *             on order0_.delivery_id=delivery2_.id

     내가 사용하지 않는 필드도 select를 하고 있습니다.
     그럼 v4가 더 좋은 것 아닌가요? 재사용성에는 좋겠지, 오히려 v4가 재사용성이 구림!
     v3은 엔티티를 조회했기 때문에 다른 곳에서 데이터를 변경하거나 할 수 있는데,
     v4는 Dto를 조회했기 때문에 재사용성이 좀 구리다는 차이. 또 Dto로 받는 쿼리가 코드상으로 조금 지저분함 ㅜ
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        // 쿼리가 1번 나갑니다! 페치조인으로 성능을 끌어올렸다!
        return orderRepository.findAllWithMemberDelivery()
                              .stream().map(SimpleOrderDto::new)
                              .collect(toList());
    }

    // JPA에서 바로 DTO로 뽑아보자.
    /**
     *     select
     *         order0_.order_id as col_0_0_,
     *         member1_.name as col_1_0_,
     *         order0_.order_date as col_2_0_,
     *         order0_.status as col_3_0_,
     *         delivery2_.city as col_4_0_,
     *         delivery2_.street as col_4_1_,
     *         delivery2_.zipcode as col_4_2_
     *     from
     *         orders order0_
     *     inner join
     *         member member1_
     *             on order0_.member_id=member1_.member_id
     *     inner join
     *         delivery delivery2_
     *             on order0_.delivery_id=delivery2_.id
     *   내가 원하는 것만 select하고 있음, select 절이 줄어들었다.(애플리케이션 네트워크 용량 최적화, 그러나 생각보다 미비함ㅎㅎ)
     *  화면에는 최적화겠지만, 재사용성에 문제가 있다.
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
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
