package com.jpashop.dolphago.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jpashop.dolphago.domain.shop.Company;
import com.jpashop.dolphago.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("/company/{id}")
    public Company findOne(@PathVariable("id") Long id) {
        Company company = companyService.findCompany(id);
        // 프록시 초기화, 지연로딩 가능
        company.getEmployeeList().forEach(employee -> {
            System.out.println("employee.getName() = " + employee.getName());
        });
        return company;
    }
}
