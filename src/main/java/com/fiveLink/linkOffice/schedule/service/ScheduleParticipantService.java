package com.fiveLink.linkOffice.schedule.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fiveLink.linkOffice.meeting.domain.MeetingParticipant;
import com.fiveLink.linkOffice.meeting.domain.MeetingParticipantDto;
import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.schedule.domain.ScheduleParticipant;
import com.fiveLink.linkOffice.schedule.domain.ScheduleParticipantDto;
import com.fiveLink.linkOffice.schedule.repository.ScheduleParticipantRepository;

@Service
public class ScheduleParticipantService {
	
    private final ScheduleParticipantRepository scheduleParticipantRepository;
    private final MemberRepository memberRepository;
    
    @Autowired
    public ScheduleParticipantService(ScheduleParticipantRepository scheduleParticipantRepository, MemberRepository memberRepository) {
        this.scheduleParticipantRepository = scheduleParticipantRepository;
        this.memberRepository = memberRepository;
    }
    
    // 일반 - 참여자 수정
    public List<ScheduleParticipantDto> getParticipantsByscheduleNo(Long scheduleNo) { 
        List<ScheduleParticipant> participants = scheduleParticipantRepository.findParticipantsByScheduleNo(scheduleNo);
 
        return participants.stream().map(participant -> { 
            String memberName = memberRepository.findById(participant.getMemberNo())
                                               .map(Member::getMemberName)
                                               .orElse("사원");
            String positionName = "직위";
            String departmentName = "부서";
            
            Long memberNo = participant.getMemberNo();
            
            List<Object[]> memberInfo = memberRepository.findMemberWithDepartmentAndPosition(memberNo); 
            
            Object[] row = memberInfo.get(0);  
            positionName = (String) row[1];   
            departmentName = (String) row[2]; 
              
            return ScheduleParticipantDto.builder()
                    .schedule_participant_no(participant.getScheduleParticipantNo())
                    .schedule_no(participant.getScheduleNo())
                    .member_no(participant.getMemberNo())
                    .schedule_participant_status(participant.getScheduleParticipantStatus())
                    .memberName(memberName)  
                    .positionName(positionName)   
                    .departmentName(departmentName)
                    .build();
        }).collect(Collectors.toList());
    }
    
    // 일반 - 참여자 수정
    @Transactional
    public void updateParticipantStatus(ScheduleParticipantDto participant) {
    	ScheduleParticipant entity = scheduleParticipantRepository.findById(participant.getSchedule_participant_no())
                .orElseThrow(() -> new IllegalArgumentException("참여자를 찾을 수 없습니다." + participant.getSchedule_participant_no()));
        entity.setScheduleParticipantStatus(participant.getSchedule_participant_status());
        scheduleParticipantRepository.save(entity);
    }
    
    // 일반 - 참여자 수정
    public void save(ScheduleParticipantDto participant) {
    	scheduleParticipantRepository.save(participant.toEntity());
    }
     
}
