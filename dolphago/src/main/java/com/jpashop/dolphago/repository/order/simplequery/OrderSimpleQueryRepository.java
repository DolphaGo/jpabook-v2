package com.jpashop.dolphago.repository.order.simplequery;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository { // Dto용 쿼리(성능 최적화용)는 패키지를 분리해서 따로 구현해놓자. 유지 보수성에 도움이 된다.

    private final EntityManager em;

    // DTO가 여기 오면, API 스펙이 아예 Repository로 들어가는 것과 다름 없다.
    // 그래서 성능 최적화와 같이 DTO를 사용할 때는 쿼리용 패키지를 따로 만드는 것이 좋다!
    public List<OrderSimpleQueryDto> findOrderDtos() {
        // 엔티티나 Value Object는 기본적으로 JPQL에서 반환할 수 있어요.
        // 대신 Dto와 같은 것은 안돼요. 그래서 Dto를 리턴하고 싶다면 new + 경로를 써줘야 합니다.
        return em.createQuery("select new com.jpashop.dolphago.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
                              + " from Order o join o.member m join o.delivery d", OrderSimpleQueryDto.class).getResultList();
    }


}
