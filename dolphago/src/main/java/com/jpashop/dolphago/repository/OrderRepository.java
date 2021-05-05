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

    /**
     * 페치조인 최적화 방법
     * toOne 관계는 fetch Join을 쓰고, 컬렉션은 지연로딩을 하라.
     * toOne 관계는 Row수 뻥튀기에 영향을 주지 않으니까
     * 그리고 batchSize를 지정하라. 왠만하면 글로벌 페치 전략을 추천한다.
     */
    public List<Order> findAllWithMemberDelivery() { // toOne 관계를 페치조인한 것이기 때문에 페이징할 수 있음
        return em.createQuery("select o from Order o "
                              + "join fetch o.member m "
                              + "join fetch o.delivery d", Order.class)
                 .getResultList();
    }

    // 이 함수가 바로 위 toOne fetch join 관계에서 페이징 처리한 것. order 기준이기에 잘 된다.
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select o from Order o "
                              + "join fetch o.member m "
                              + "join fetch o.delivery d", Order.class)
                 .setFirstResult(offset)
                 .setMaxResults(limit)
                 .getResultList();

//        // 그냥 다음과 같이 해도 BatchSize에 의해 member, delivery가 최적화가 됩니다.
//        // 대신 아무래도 네트워크를 더 많이 타게 되죠. 그래서 toOne관계는 페치조인 때리셔도 됩니다.
//        return em.createQuery("select o from Order o ", Order.class)
//                 .setFirstResult(offset)
//                 .setMaxResults(limit)
//                 .getResultList();

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
