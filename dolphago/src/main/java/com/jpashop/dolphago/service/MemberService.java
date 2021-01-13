package com.jpashop.dolphago.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpashop.dolphago.domain.shop.Member;
import com.jpashop.dolphago.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
//@Transactional //트랜잭션 안에서 데이터 변경해야 한다.
@Transactional(readOnly = true) //조회를 하는 로직에서 readOnly = true 옵션을 주면, Jpa가 조회하는 곳에서 좀 더 좋은 성능을 냄.
@RequiredArgsConstructor // final 이 있는 필드만 가지고 생성자를 만들어 줌.
public class MemberService {

    //final을 붙여주는 것을 추천한다. 초기화를 안해주면 컴파일 시점에 체크를 할 수 있고, 추후 변경하지 않기 때문에 final 권장
    private final MemberRepository memberRepository;

    /**
     *
     // @Autowired private MemberRepository memberRepository;
     * Autowired 방식의 단점
     *  -> 코드를 바꾸지 못함. 테스트를 할 때 바꿔야할 때가 있는데 Access할 방법이 없음.
     *  -> 즉, 코드에 주입할 수 있는 게 유동적이지 않음.(고정)
     *
     *  Setter Injection
     *  장점 : 테스트 코드를 작성할 때 개발자가 직접 빈을 주입해줄 수 있음.
     *  단점 : 애플리케이션 로딩 시점에 실제 잘 동작하고 있는데 굳이 Setter Injection이 필요가 없다.
     *
     *  권장 : 생성자 Injection
     *
     *  //@Autowired : Spring이 생성자가 하나만 있는 경우, Autowired가 없어도 자동으로 Injection을 해준다.
     *     public MemberService(MemberRepository memberRepository){
     *         this.memberRepository = memberRepository;
     *     }
     */

    //회원 가입
    @Transactional //메서드에 붙은 것이 더 우선순위이므로, 클래스에 붙어있는 readOnly는 적용되지 않는다.
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id); //영속상태
        member.setName(name); //Dirty-checking
    }
}
