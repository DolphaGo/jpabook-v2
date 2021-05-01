package com.jpashop.dolphago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

@SpringBootApplication
public class DolphagoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DolphagoApplication.class, args);
    }

    @Bean
    Hibernate5Module hibernate5Module() {
        return new Hibernate5Module(); // 레이지로딩을 호출해서 초기화된 녀석만 API로 반환이 됨
    }
}
