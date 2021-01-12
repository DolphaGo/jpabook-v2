package com.jpashop.dolphago;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.jpashop.dolphago.service.TestService;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
public class DolphagoApplication {

    public static void main(String[] args) {
		SpringApplication.run(DolphagoApplication.class, args);
    }

}
