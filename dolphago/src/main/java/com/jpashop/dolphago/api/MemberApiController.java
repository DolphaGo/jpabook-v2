package com.jpashop.dolphago.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.jpashop.dolphago.domain.shop.Member;
import com.jpashop.dolphago.service.MemberService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /**
     * 회원 등록
     */
    //api에는 entity를 절대로 노출하지 말 것. api 요청 스펙에 따라서 별도의 dto를 반드시 만들어라.
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member=new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 회원 수정
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMmberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){

        //수정할 땐 가급적 변경 감지를 사용해라.
        //커맨드와 쿼리를 분리할 것.
        memberService.update(id,request.getName()); //커맨드
        Member findMember = memberService.findOne(id);//쿼리
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /**
     * 회원 조회
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result<>(collect);
    }

    //한 번 감싸서 리턴해야 유연성이 증가한다. 바로 리턴하면 []로 반환되기에 유연성이 확 떨어짐.
    //내가 노출할 것만 api외부에 노출하는 것.
    @Data
    @AllArgsConstructor
    static class Result<T>{
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }


    @Data
    static class UpdateMemberRequest{
        private String name;
    }


    //DTO에는 Lombok을 많이 활용함. Entity는 제한적으로 활용함
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }


    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest{
        private String name;
    }

}
