package com.jpashop.dolphago.domain.shop;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Getter @Setter
@Entity
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotEmpty(message = "이름이 있어야 합니다.")
    private String name;

    @Embedded
    private Address address;

    @JsonIgnore // Json을 사용하지 않으려면 이를 이용하면 되지만, 굉장히 위험한 일. 모든 케이스를 대응할 수 없다.
    @OneToMany(mappedBy = "member")
    private List<Order> orders=new ArrayList<>();
}
