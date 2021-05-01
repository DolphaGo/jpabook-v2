package com.jpashop.dolphago.repository;

import com.jpashop.dolphago.domain.shop.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearch {

    private String memberName; // 회원 이름
    private OrderStatus orderStatus; // 주문 상태 [ ORDER, CANCEL ]

    public OrderSearch() {
//        memberName = "userA";
//        orderStatus = OrderStatus.ORDER;
    }

    public OrderSearch(String memberName, OrderStatus orderStatus) {
        this.memberName = memberName;
        this.orderStatus = orderStatus;
    }
}
