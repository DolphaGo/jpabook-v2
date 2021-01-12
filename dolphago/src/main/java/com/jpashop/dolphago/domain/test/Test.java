package com.jpashop.dolphago.domain.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Test {

    @Id @GeneratedValue
    @Column(name = "test_id")
    private Long id;
    private String message;
    @Enumerated(EnumType.STRING)
    private TestEnum testEnum;

    public Test(String message, TestEnum testEnum) {
        this.message = message;
        this.testEnum = testEnum;
    }
}
