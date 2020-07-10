package com.jpashop.dolphago.domain.test;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@NoArgsConstructor
public class TestForm {
    private String message;
    private TestEnum testEnum;

    public TestForm(String message, TestEnum testEnum) {
        this.message = message;
        this.testEnum = testEnum;
    }
}
