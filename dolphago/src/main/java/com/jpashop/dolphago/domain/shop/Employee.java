package com.jpashop.dolphago.domain.shop;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Employee {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    public Employee(String name) {
        this.name = name;
    }

    public void join(Company company){
        this.company = company;
        company.getEmployeeList().add(this);
    }
}
