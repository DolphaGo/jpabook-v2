package com.jpashop.dolphago.domain.shop;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Delivery {
    @Id @GeneratedValue
    private Long id;

//    @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY) //order -> delivery가 더 많이 사용되기 때문
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
}