package com.jpashop.dolphago.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpashop.dolphago.domain.test.Test;
import com.jpashop.dolphago.domain.test.TestEnum;
import com.jpashop.dolphago.domain.test.TestRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;

    @Value("${test.nickname}")
    private String nickname;

    @PostConstruct
    public void init() {
        this.nickname = this.nickname + "입니다";
    }

    public String say(){
        return this.nickname;
    }

    @Transactional
    public void updateStatus(Long testId, TestEnum testEnum) {
        log.info("변경할 상태={}", testEnum);
        testRepository.updateStatus(testId, testEnum);
    }

    public void save(String message, TestEnum testEnum) {
        final Test test = new Test(message, testEnum);
        testRepository.save(test);
    }
}
