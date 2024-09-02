package com.fiveLink.linkOffice.common.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.repository.MemberRepository;

@Service
public class OrganizationService {

    private final MemberRepository memberRepository;

    @Autowired
    public OrganizationService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getMembersByDepartment(Long departmentNo) {
        return memberRepository.findByDepartmentNo(departmentNo);
    }

    public List<Member> getMembersByPosition(Long positionNo) {
        return memberRepository.findByPositionNo(positionNo);
    }
}
