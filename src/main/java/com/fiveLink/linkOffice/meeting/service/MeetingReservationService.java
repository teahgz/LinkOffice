package com.fiveLink.linkOffice.meeting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.meeting.domain.MeetingReservation;
import com.fiveLink.linkOffice.meeting.domain.MeetingReservationDto;
import com.fiveLink.linkOffice.meeting.repository.MeetingReservationRepository;
import com.fiveLink.linkOffice.member.domain.Member;
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
        List<Object[]> reservations = meetingReservationRepository.findReservations(date, 0L);
        List<MeetingReservationDto> dtoList = new ArrayList<>();
        
        for (Object[] row : reservations) {
            MeetingReservation reservation = (MeetingReservation) row[0];  
            Member member = (Member) row[1];   
            String positionName = (String) row[2];  
            String departmentName = (String) row[3]; 
            String meetingName = meetingService.getMeetingNameById(reservation.getMeetingNo());
            
            MeetingReservationDto dto = MeetingReservationDto.builder()
                    .meeting_reservation_no(reservation.getMeetingReservationNo())
                    .meeting_no(reservation.getMeetingNo())
                    .member_no(reservation.getMemberNo())
                    .meeting_reservation_date(reservation.getMeetingReservationDate())
                    .meeting_reservation_start_time(reservation.getMeetingReservationStartTime())
                    .meeting_reservation_end_time(reservation.getMeetingReservationEndTime())
                    .meeting_reservation_purpose(reservation.getMeetingReservationPurpose())
                    .meeting_reservation_create_date(reservation.getMeetingReservationCreateDate())
                    .meeting_reservation_update_date(reservation.getMeetingReservationUpdateDate())
                    .meeting_reservation_status(reservation.getMeetingReservationStatus())
                    .meeting_name(meetingName)  
                    .member_name(member.getMemberName()) 
                    .position_name(positionName) 
                    .department_name(departmentName) 
                    .build();
            
            dtoList.add(dto);
        } 
        return dtoList;
    } 


}