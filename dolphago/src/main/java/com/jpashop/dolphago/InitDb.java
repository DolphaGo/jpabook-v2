package com.jpashop.dolphago;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jpashop.dolphago.domain.Address;
import com.jpashop.dolphago.domain.Book;
import com.jpashop.dolphago.domain.Delivery;
import com.jpashop.dolphago.domain.Member;
import com.jpashop.dolphago.domain.Order;
import com.jpashop.dolphago.domain.OrderItem;

import lombok.RequiredArgsConstructor;

/**
 * 총 주문 2개
 * userA
 * ** JPA1 Book
 * ** JPA2 Book
 *
 * userB
 * ** Spring1 Book
 * ** Spring2 Book
 *
 */
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{
        private final EntityManager em;
        public void dbInit1(){
            Member member = getMember("서울", "1", "1111");

            Book book1=new Book();
            book1.setName("JPA1 Book");
            book1.setPrice(10000);
            book1.setStockQuantity(100);
            em.persist(book1);

            Book book2=new Book();
            book2.setName("JPA2 Book");
            book2.setPrice(20000);
            book2.setStockQuantity(100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 10000, 2);

            Delivery delivery=new Delivery();
            delivery.setAddress(member.getAddress());
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2(){
            Member member = getMember("대전", "1", "1111");

            Book book1=new Book();
            book1.setName("Spring1 Book");
            book1.setPrice(30000);
            book1.setStockQuantity(100);
            em.persist(book1);

            Book book2=new Book();
            book2.setName("Spring2 Book");
            book2.setPrice(40000);
            book2.setStockQuantity(100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 30000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery=new Delivery();
            delivery.setAddress(member.getAddress());
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Member getMember(String city, String street, String zipcode) {
            Member member = new Member();
            member.setName("userA");
            member.setAddress(new Address(city, street, zipcode));
            em.persist(member);
            return member;
        }
    }
}
