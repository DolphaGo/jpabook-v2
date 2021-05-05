package com.jpashop.dolphago.repository.order.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

// 엔티티가 아닌, 화면에 fit한 쿼리들을 담을 레포지토리
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // Query N번, 이 문제는 아직 N+1문제가 있다.
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new com.jpashop.dolphago.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                + " from OrderItem oi"
                + " join oi.item i"
                + " where oi.order.id = :orderId", OrderItemQueryDto.class)
                 .setParameter("orderId", orderId)
                 .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                // 컬렉션을 바로 넣을 수 없음(orderItems), 데이터를 플랫하게 한 줄로밖에 못넣음
                // 1:N 관계에서는 DTO 만들 때 못 넣음
                "select new com.jpashop.dolphago.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
                + " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderQueryDto.class).getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();
        //기존 V4의 단점이 Loop를 도는 것이였다. 이를 한방에 가져와서 개선해보려고 한다.
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findORderItemMap(toOrderIds(result));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
        /**
         * 무엇이 개선되었는가?
         * 쿼리를 총 2번만 날리고, 메모리에서 처리를 하는 것임
         */
    }

    private Map<Long, List<OrderItemQueryDto>> findORderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new com.jpashop.dolphago.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                + " from OrderItem oi"
                + " join oi.item i"
                + " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                                               .setParameter("orderIds", orderIds)
                                               .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                                                                    .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                     .map(o -> o.getOrderId())
                     .collect(Collectors.toList());
    }
}
