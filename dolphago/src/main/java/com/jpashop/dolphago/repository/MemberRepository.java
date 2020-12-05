package com.jpashop.dolphago.repository;

import com.jpashop.dolphago.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    /**
     * 원래 Spring에서 EntityManager는 @PersistenceContext로만 EntityManager를 주입 받을 수 있었는데,
     * SpringBoot Data JPA에서는 @Autowired도 지원을 해준다.
     * 그래서 생성자 주입 방식이 가능했지고 생성자가 하나이므로 @Autowired가 생략도 되고
     * final로 올려놓고, @RequiredArgsConstructor로 EntityManager를 주입받을 수 있게 되는 것이다.
     */
    private final EntityManager em;
//    @PersistenceContext
//    private EntityManager em; // Spring이 생성한 Entity Manager를 주입해준다.

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class , id);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name",name)
                .getResultList();
    }
}
