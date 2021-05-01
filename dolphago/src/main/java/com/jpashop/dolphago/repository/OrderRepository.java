package com.jpashop.dolphago.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.jpashop.dolphago.domain.shop.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepository {    // 여기는 엔티티 스펙만 쿼리하자

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = " + orderSearch.getOrderStatus();
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name = " + orderSearch.getMemberName();
        }

        return em.createQuery(jpql, Order.class).getResultList();

//        //동적 쿼리는 QuaryDSL을 사용해서 해결하자~
//        log.info("들어오는 orderSearch Type={}", orderSearch.getOrderStatus());
//        return em.createQuery("select o from Order o join Member m on o.member = m where o.status = :status and o.member.name like :name", Order.class)
//                 .setParameter("name", orderSearch.getMemberName())
//                 .setParameter("status", orderSearch.getOrderStatus())
//                 .setMaxResults(1000)
//                 .getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order o join fetch o.member m join fetch o.delivery d", Order.class).getResultList();
    }

    public List<Order> findAllWithItem() {
        // 페치 조인도 사실은 그냥 조인의 연속이다.
        // 조인을 여러개 하고, Select도 여러개 해서 반환하는 것
        // 그래서 주문이 중복될 수 있다. (조인으로 인해) -> distinct로 구분하자
        return em.createQuery(
                "select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class)
                 .setFirstResult(1)
                 .setMaxResults(100)
                 .getResultList();
    }
}
