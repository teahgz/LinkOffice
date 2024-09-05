package com.fiveLink.linkOffice.member.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.repository.MemberRepository;

import jakarta.transaction.Transactional;

@Service
public class MemberService {
	
	private final MemberRepository memberRepository;
	
	@Autowired
	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	
	public List<MemberDto> getMemberByNumber(String memberNumber) {  
		 List<Object[]> results = memberRepository.findMemberNumber(memberNumber);
		 return results.stream().map(result -> {
	            Member member = (Member) result[0];
	            String positionName = (String) result[1];
	            String departmentName = (String) result[2];

	            return MemberDto.builder()
	                    .member_no(member.getMemberNo())
	                    .member_number(member.getMemberNumber())
	                    .member_pw(member.getMemberPw())
	                    .member_name(member.getMemberName())
	                    .member_national(member.getMemberNational())
	                    .member_internal(member.getMemberInternal())
	                    .member_mobile(member.getMemberMobile())
	                    .department_no(member.getDepartmentNo())
	                    .position_no(member.getPositionNo())
	                    .department_name(departmentName)
	                    .position_name(positionName)
	                    .member_address(member.getMemberAddress())
	                    .member_hire_date(member.getMemberHireDate())
	                    .member_end_date(member.getMemberEndDate())
	                    .member_create_date(member.getMemberCreateDate())
	                    .member_update_date(member.getMemberUpdateDate())
	                    .member_ori_profile_img(member.getMemberOriProfileImg())
	                    .member_new_profile_img(member.getMemberNewProfileImg())
	                    .member_ori_digital_img(member.getMemberOriDigitalImg())
	                    .member_new_digital_img(member.getMemberNewDigitalImg())
	                    .member_status(member.getMemberStatus())
	                    .member_additional(member.getMemberAdditional())
	                    .build();
	        }).collect(Collectors.toList());
	    }
	
	// mypage 정보 조회
	 public List<MemberDto> getMembersByNo(Long memberNo) {
	        List<Object[]> results = memberRepository.findMemberWithDepartmentAndPosition(memberNo);

	        return results.stream().map(result -> {
	            Member member = (Member) result[0];
	            String positionName = (String) result[1];
	            String departmentName = (String) result[2];

	            return MemberDto.builder()
	                    .member_no(member.getMemberNo())
	                    .member_number(member.getMemberNumber())
	                    .member_pw(member.getMemberPw())
	                    .member_name(member.getMemberName())
	                    .member_national(member.getMemberNational())
	                    .member_internal(member.getMemberInternal())
	                    .member_mobile(member.getMemberMobile())
	                    .department_no(member.getDepartmentNo())
	                    .position_no(member.getPositionNo())
	                    .department_name(departmentName)
	                    .position_name(positionName)
	                    .member_address(member.getMemberAddress())
	                    .member_hire_date(member.getMemberHireDate())
	                    .member_end_date(member.getMemberEndDate())
	                    .member_create_date(member.getMemberCreateDate())
	                    .member_update_date(member.getMemberUpdateDate())
	                    .member_ori_profile_img(member.getMemberOriProfileImg())
	                    .member_new_profile_img(member.getMemberNewProfileImg())
	                    .member_ori_digital_img(member.getMemberOriDigitalImg())
	                    .member_new_digital_img(member.getMemberNewDigitalImg())
	                    .member_status(member.getMemberStatus())
	                    .member_additional(member.getMemberAdditional())
	                    .build();
	        }).collect(Collectors.toList());
	    }
	 
	 // [서혜원] 부서별 사원
	 public List<MemberDto> getMembersByDepartmentNo(Long departmentNo) {
	    List<Member> members = memberRepository.findByDepartmentNo(departmentNo);
	    return members.stream().map(member -> MemberDto.builder()
	            .memberId(member.getMemberNo())
	            .memberName(member.getMemberName())
	            .departmentNo(member.getDepartment().getDepartmentNo())  
	            .build()
	    ).collect(Collectors.toList());
	}
  
	// [서혜원] 부서 관리 memberdto
	public Long getLoggedInMemberNo() {
    	org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  
 
        Member member = memberRepository.findByMemberNumber(username);

        if (member != null) {
            return member.getMemberNo();  
        } else {
            throw new RuntimeException("로그인한 사용자 정보를 찾을 수 없습니다.");
        }
    }
     
	// [서혜원] 직위 번호별 사원 조회
	public List<MemberDto> getMembersByPositionNo(Long positionNo) {
	    List<Member> members = memberRepository.findByPositionNo(positionNo);
	    return members.stream()
	        .map(member -> MemberDto.builder()
	            .memberId(member.getMemberNo())
	            .memberName(member.getMemberName())
	            .positionNo(member.getPosition().getPositionNo()) 
	            .build()
	        )
	        .collect(Collectors.toList());
	}
 
    // [전주영] 전자결재 서명 dto 조회 
    public MemberDto selectMemberOne(Long memberNo) {
    	Member member = memberRepository.findByMemberNo(memberNo);
    	MemberDto dto = new MemberDto().toDto(member);
    	return dto;
    }
    
    // 전자결재 서명 update
    @Transactional
    public Member updateMemberDigital(MemberDto dto) {
    	MemberDto temp = selectMemberOne(dto.getMember_no());
    	if(dto.getMember_ori_digital_img() != null && "".equals(dto.getMember_ori_digital_img()) == false) {
    		temp.setMember_ori_digital_img(dto.getMember_ori_digital_img());
    		temp.setMember_new_digital_img(dto.getMember_new_digital_img());
    	} 
    	
    	Member member = temp.toEntity();
    	
    	Member result = memberRepository.save(member);
    	return result;
    } 
    
    // 프로필 및 비밀번호, 주소 변경 
    @Transactional
    public Member updateMemberProfile(MemberDto dto) {
    	MemberDto temp = selectMemberOne(dto.getMember_no());
    	System.out.println("memberService"+temp);
    	
    	Member member = dto.toEntity();
    	System.out.println(dto);
    	Member result = memberRepository.save(member);
    	
    	return result;
    	
    }
}