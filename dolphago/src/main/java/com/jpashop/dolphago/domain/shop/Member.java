package com.jpashop.dolphago.domain.shop;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotEmpty(message = "이름이 있어야 합니다.")
    private String name;

    @Embedded
    private Address address;

    //    @JsonIgnore // Json을 사용하지 않으려면 이를 이용하면 되지만, 굉장히 위험한 일. 모든 케이스를 대응할 수 없다.
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
