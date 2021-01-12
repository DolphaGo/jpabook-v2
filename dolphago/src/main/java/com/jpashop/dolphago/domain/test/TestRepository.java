package com.jpashop.dolphago.domain.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TestRepository extends JpaRepository<Test,Long> {
    @Modifying
    @Query("UPDATE Test t SET t.testEnum = :testEnum WHERE t.id = :testId")
    void updateStatus(@Param("testId") Long testId, @Param("testEnum") TestEnum testEnum);
}
