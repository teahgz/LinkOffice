package com.fiveLink.linkOffice.member.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;

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
  
}