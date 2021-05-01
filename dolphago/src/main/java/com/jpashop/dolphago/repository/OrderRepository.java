package com.jpashop.dolphago.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.jpashop.dolphago.domain.shop.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        //동적 쿼리는 QuaryDSL을 사용해서 해결하자~
        log.info("들어오는 orderSearch Type={}", orderSearch.getOrderStatus());
        return em.createQuery("select o from Order o join Member m on o.member = m where o.status = :status and o.member.name like :name", Order.class)
                 .setParameter("name", orderSearch.getMemberName())
                 .setParameter("status", orderSearch.getOrderStatus())
                 .setMaxResults(1000)
                 .getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order o join fetch o.member m join fetch o.delivery d", Order.class).getResultList();
    }
}
