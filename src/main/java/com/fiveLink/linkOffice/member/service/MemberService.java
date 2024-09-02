package com.fiveLink.linkOffice.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.repository.MemberRepository;

@Service
public class MemberService {
	
	private final MemberRepository memberRepository;
	
	@Autowired
	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	
	public MemberDto getMemberByNumber(String memberNumber) {  
		Member member = memberRepository.findByMemberNumber(memberNumber);
		
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
	}
}