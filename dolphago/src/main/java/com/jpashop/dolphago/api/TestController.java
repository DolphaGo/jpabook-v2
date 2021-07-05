package com.jpashop.dolphago.api;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpashop.dolphago.domain.test.TestForm;
import com.jpashop.dolphago.service.TestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TestController {
    private final TestService testService;

    @GetMapping("/test")
    public String say() {
        return testService.say();
    }

    @PostMapping("/test/create")
    public void create(@Valid @RequestBody TestForm testForm) {
        log.info("입력 ={}", testForm);
        testService.save(testForm.getMessage(), testForm.getTestEnum());
    }

    @PutMapping("/test/modify/{id}")
    public void modify(@PathVariable Long id, @Valid @RequestBody TestForm testForm) {
        testService.updateStatus(id, testForm.getTestEnum());
    }

}
