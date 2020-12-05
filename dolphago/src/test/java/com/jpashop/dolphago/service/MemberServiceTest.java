package com.jpashop.dolphago.service;

import com.jpashop.dolphago.domain.Member;
import com.jpashop.dolphago.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional //테스트에서는 롤백 true가 기본
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    @Rollback(value = false)
    public void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("DolphaGo");

        //when
        Long savedId = memberService.join(member);

        //then
        em.flush(); //insert query를 출력해서 볼 수 있다.
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    public void 중복회원예외() throws Exception{
        //given
        Member member1=new Member();
        member1.setName("kim");

        Member member2=new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);

//        try{
        memberService.join(member2); //예외가 발생해야 한다!!!
//        }catch (IllegalStateException e){
//            System.out.println("캐치!");
//            return;
//        }


        //then
//        fail("예외가 발생해야 한다.");
    }


}