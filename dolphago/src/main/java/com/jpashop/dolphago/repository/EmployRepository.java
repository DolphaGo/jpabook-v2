package com.jpashop.dolphago.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpashop.dolphago.domain.shop.Employee;

public interface EmployRepository extends JpaRepository<Employee, Long> {
}
