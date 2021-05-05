package com.jpashop.dolphago.repository.order.query;

import java.util.List;

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
}
