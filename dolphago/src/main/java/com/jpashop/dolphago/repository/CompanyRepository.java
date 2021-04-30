package com.jpashop.dolphago.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpashop.dolphago.domain.shop.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
