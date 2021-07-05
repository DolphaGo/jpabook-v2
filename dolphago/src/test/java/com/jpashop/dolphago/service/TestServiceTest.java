package com.jpashop.dolphago.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@EnableAutoConfiguration
@SpringBootTest
class TestServiceTest {

    @Autowired
    TestService testService;

    @DisplayName("property 값 주입이후, postConstruct 값 변환 세팅")
    @Test
    void test() {
        System.out.println(testService.say());
    }
}