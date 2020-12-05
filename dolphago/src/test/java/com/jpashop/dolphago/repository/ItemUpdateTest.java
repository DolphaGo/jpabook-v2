package com.jpashop.dolphago.repository;

import com.jpashop.dolphago.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

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