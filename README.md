## 기록

JPQL에서 fetch Join으로 1+N 문제를 해결하려고 할 때

1. DTO로 커팅해서 가져오는가

2. 엔티티를 가져와서 Application 단에서 데이터를 정리하는가

2가지의 상황이 있을 수 있는데, 각각의 Trade-off가 있다.

1의 경우 select절이 줄어들기 때문에 애플리케이션 네트워크 용량 최적화를 할 수 있다. 그러나 `재사용성` 에 문제가 있다.

2의 경우는 보여질 데이터가 아님에도 모든 데이터를 끌고 오기에 약간은 불필요한 감이 있지만, `재사용성` 은 1에 비해 높다.

<u>그렇지만, 네트워크 용량이 그렇게나 많이 차이날까?</u>

사실 성능을 먹는 것은 join에서 대부분 걸린다. 전체적으로 바라봤을 때는 select에 몇개 더 들어간다고 해서 성능 이슈는 거의 없다. 실제 성능을 좌지우지 하는 부분은 인덱스 쿼리이다. 그럼에도 고객 트래픽이 엄청난 API라면 1과 같이 최적화를 하는 것이 좋겠다.

**권장 순서**

1. 엔티티 조회방식으로 우선 접근
   1. 페치조인으로 쿼리 수를 최적화
   2. 컬렉션 최적화
      1. 페이징 필요O : hibernate.default_batch_fetch_size , @BatchSize 로 최적화 
      2. 페이징 필요X : 페치 조인 사용

2. 엔티티 조회 방식으로 해결이 안되면 DTO조회 방식 사용
3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate



```java
private class OrderDto {
  private Long orderId;
  private String name;
  private LocalDateTime orderDate;
  private OrderStatus orderStatus;
  private Address address;
  private List<OrderItem> orderItems;
}
```

예를 들어서 Order 안에 OrderItem들이 있을 때, Order를 DTO로 만들겠다고, 해당 값들을 DTO로 복사한다. 그리고 그냥 orderDto.orderItems = order.getOrderItems() 를 하면, order에 있는 orderItem은 레이지로 발라져 있는 엔티티이기 때문에 dto.orderItems엔 null로 출력이 된다. 즉, 프록시 강제 초기화를 해줘야 한다.

근데 사실 ㅎㅎ 이렇게 엔티티를 DTO로 래핑하는 것도 좋지 않다. OrderItem 또한 DTO로 만들어줘야 한다.

완전히 Entity를 분리해줘야 한다.

```java
private class OrderDto {
  private Long orderId;
  private String name;
  private LocalDateTime orderDate;
  private OrderStatus orderStatus;
  private Address address;
  private List<OrderItemDto> orderItems;
}
```



---

distinct로 fetch join시 레이블이 뻥튀기 되는 것을 줄여줄 수 있다.

그러나 DB 쿼리의 결과를 뽑을 때는 distinct가 먹지 않음(hibernate가 만들어준 distict 조회 쿼리를 db에 날려보면 distict 처리가 되지 않았음을 확인할 수 있음)

- 왜 Why ? DB 단에서 distict는 컬럼 값이 완전히 같아야 처리가 됨

즉, JPA에서 자체적으로 distict가 있으면, Order가 같은 ID값이면 중복을 제거해줍니다.

```java
public List<Order> findAllWithItem() {
  // 페치 조인도 사실은 그냥 조인의 연속이다.
  // 조인을 여러개 하고, Select도 여러개 해서 반환하는 것
  // 그래서 주문이 중복될 수 있다. (조인으로 인해) -> distinct로 구분하자
  return em.createQuery(
    "select distinct o from Order o" + // Order가 Root
    " join fetch o.member m" +
    " join fetch o.delivery d" +
    " join fetch o.orderItems oi" +
    " join fetch oi.item i", Order.class).getResultList();
}
```

distinct의 2가지 기능

- DB에 distict를 날려주고
- Root (엔티티)가 중복인 경우에 이 중복을 걸러준다.



---

Fetch Join은 페이징이 안된다!

```java
public List<Order> findAllWithItem() {
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
```

위와 같은 페이징 쿼리를 날리고, 호출을 해보면 다음과 같은 로그가 나온다.

```web-idl
2021-05-02 06:59:15.768  WARN 80161 --- [nio-8080-exec-1] o.h.h.internal.ast.QueryTranslatorImpl   : HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
```

그리고 실행된 쿼리를 보면 다음과 같다.

```sql
    select
        distinct order0_.order_id as order_id1_6_0_,
        member1_.member_id as member_i1_4_1_,
        delivery2_.id as id1_2_2_,
        orderitems3_.order_item_id as order_it1_5_3_,
        item4_.item_id as item_id2_3_4_,
        order0_.delivery_id as delivery4_6_0_,
        order0_.member_id as member_i5_6_0_,
        order0_.order_date as order_da2_6_0_,
        order0_.status as status3_6_0_,
        member1_.city as city2_4_1_,
        member1_.street as street3_4_1_,
        member1_.zipcode as zipcode4_4_1_,
        member1_.name as name5_4_1_,
        delivery2_.city as city2_2_2_,
        delivery2_.street as street3_2_2_,
        delivery2_.zipcode as zipcode4_2_2_,
        delivery2_.status as status5_2_2_,
        orderitems3_.count as count2_5_3_,
        orderitems3_.item_id as item_id4_5_3_,
        orderitems3_.order_id as order_id5_5_3_,
        orderitems3_.order_price as order_pr3_5_3_,
        orderitems3_.order_id as order_id5_5_0__,
        orderitems3_.order_item_id as order_it1_5_0__,
        item4_.name as name3_3_4_,
        item4_.price as price4_3_4_,
        item4_.stock_quantity as stock_qu5_3_4_,
        item4_.artist as artist6_3_4_,
        item4_.etc as etc7_3_4_,
        item4_.author as author8_3_4_,
        item4_.isbn as isbn9_3_4_,
        item4_.actor as actor10_3_4_,
        item4_.director as directo11_3_4_,
        item4_.dtype as dtype1_3_4_ 
    from
        orders order0_ 
    inner join
        member member1_ 
            on order0_.member_id=member1_.member_id 
    inner join
        delivery delivery2_ 
            on order0_.delivery_id=delivery2_.id 
    inner join
        order_item orderitems3_ 
            on order0_.order_id=orderitems3_.order_id 
    inner join
        item item4_ 
            on orderitems3_.item_id=item4_.item_id
```

ㅇ? limit과 같은 쿼리가 없다. 메모리 상에서 페이징을 한다는 소리.

그렇다면, 굉장히 많은 데이터를 끌고 오면, OOM이 날 가능성이 있다.

DB입장에서는 데이터가 뻥튀기가 됨. 여기서는 OrderItem 기준으로 데이터가 뻥튀기가 되어서 데이터를 못맞추게 된다. (Order 기준의 페이징이 아니라!!) 그래서 어쩔 수 없이 메모리 상에서 OOM 문제가 있다.

1:N 관계에서 페치 조인을 쓰신다면 **페이징 불가**합니다.

페치 조인으로 SQL이 1번만 실행됨
distinct 를 사용한 이유는 1대다 조인이 있으므로 데이터베이스 row가 증가한다. 그 결과 같은 order

엔티티의 조회 수도 증가하게 된다. JPA의 distinct는 SQL에 distinct를 추가하고, 더해서 같은 엔티티가 조회되면, 애플리케이션에서 중복을 걸러준다. 이 예에서 order가 컬렉션 페치 조인 때문에 중복 조회 되는 것을 막아준다.

- 페치 조인의 단점
  - **페이징 불가능**

> 참고: 컬렉션 페치 조인을 사용하면 페이징이 불가능하다. 하이버네이트는 경고 로그를 남기면서 모든 데이터를 DB에서 읽어오고, 메모리에서 페이징 해버린다(매우 위험하다). 

> 참고: **컬렉션 페치 조인은 1개만 사용**할 수 있다. 컬렉션 둘 이상에 페치 조인을 사용하면 안된다. 데이터가 부정합하게 조회될 수 있다. (1:N:N...) 데이터가 완전 뻥튀기가 됨! 어떤 기준으로 데이터를 끌고 와야할 지 데이터의 개수가 안맞거나 정합성에 문제가 있을 수 있음.  

