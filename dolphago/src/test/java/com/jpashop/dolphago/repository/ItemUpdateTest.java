package com.jpashop.dolphago.repository;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.jpashop.dolphago.domain.Book;

@SpringBootTest
class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    public void updateTest() throws Exception {
        Book book=em.find(Book.class,1L);

        //TX
        book.setName("adasdasd");

        //변경 감지 ==
    }

}