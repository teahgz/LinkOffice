package com.fiveLink.linkOffice.meeting.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.meeting.domain.MeetingReservation;
import com.fiveLink.linkOffice.meeting.domain.MeetingReservationDto;
import com.fiveLink.linkOffice.meeting.repository.MeetingReservationRepository;
import com.fiveLink.linkOffice.member.service.MemberService;

@Service
public class MeetingReservationService {

    private final MeetingReservationRepository meetingReservationRepository;
    private final MeetingService meetingService; 
    private final MemberService memberService; 
    
    @Autowired
    public MeetingReservationService(MeetingReservationRepository meetingReservationRepository, MeetingService meetingService, MemberService memberService) {
        this.meetingReservationRepository = meetingReservationRepository;
        this.meetingService = meetingService;
        this.memberService = memberService;
    }

    // 해당 날짜 예약 정보 
    public List<MeetingReservationDto> getReservationsByDate(String date) {
        List<MeetingReservation> reservations = meetingReservationRepository.findReservations(date, 0L);
        
        return reservations.stream().map(reservation -> { 
            String meetingName = meetingService.getMeetingNameById(reservation.getMeetingNo());
            String memberName = memberService.getMemberNameById(reservation.getMemberNo());
            return MeetingReservationDto.toDto(reservation, meetingName, memberName);
        }).collect(Collectors.toList());
    }
}
