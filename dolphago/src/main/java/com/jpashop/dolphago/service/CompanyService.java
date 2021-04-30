package com.jpashop.dolphago.service;

import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpashop.dolphago.domain.shop.Company;
import com.jpashop.dolphago.domain.shop.Employee;
import com.jpashop.dolphago.repository.CompanyRepository;
import com.jpashop.dolphago.repository.EmployRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final EmployRepository employRepository;

    @Transactional
    @PostConstruct
    public void setup() {
        Company company = new Company("DolphaGo's Company");
        companyRepository.save(company);

        Employee employee1 = new Employee("DolphaGo");
        employee1.join(company);
        Employee employee2 = new Employee("AlphaGo");
        employee2.join(company);

        employRepository.save(employee1);
        employRepository.save(employee2);
    }


    public Company findCompany(Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
    }
}
