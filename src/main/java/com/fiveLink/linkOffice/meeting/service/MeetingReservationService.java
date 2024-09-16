package com.fiveLink.linkOffice.meeting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.meeting.domain.MeetingReservation;
import com.fiveLink.linkOffice.meeting.domain.MeetingReservationDto;
import com.fiveLink.linkOffice.meeting.repository.MeetingParticipantRepository;
import com.fiveLink.linkOffice.meeting.repository.MeetingReservationRepository;
import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.member.service.MemberService;

@Service
public class MeetingReservationService {

    private final MeetingReservationRepository meetingReservationRepository;
    private final MeetingService meetingService; 
    private final MemberService memberService; 
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final MemberRepository memberRepository;
    
    @Autowired
    public MeetingReservationService(MeetingReservationRepository meetingReservationRepository, MeetingService meetingService, MemberService memberService, MeetingParticipantRepository meetingParticipantRepository, MemberRepository memberRepository) {
        this.meetingReservationRepository = meetingReservationRepository;
        this.meetingService = meetingService;
        this.memberService = memberService;
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.memberRepository = memberRepository;
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
    
    // 본인 예약 정보  
    public Page<MeetingReservationDto> searchReservations(Long memberNo, String meetingNo, String searchText, String startDate, String endDate, String sortBy, Pageable pageable) { 

        Page<MeetingReservation> reservationPage = meetingReservationRepository.searchReservations(
									                memberNo, 
									                meetingNo.isEmpty() ? null : Long.parseLong(meetingNo), 
									                searchText.isEmpty() ? null : searchText, 
									                startDate.isEmpty() ? null : startDate, 
									                endDate.isEmpty() ? null : endDate, 
									                sortBy, 
									                pageable);
 
        return reservationPage.map(reservation -> {
            String meetingName = meetingService.getMeetingNameById(reservation.getMeetingNo());
            String memberName = memberService.getMemberNameById(reservation.getMemberNo());
            long participantCount = meetingParticipantRepository.countByMeetingReservationNo(reservation.getMeetingReservationNo());

            return MeetingReservationDto.builder()
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
                    .member_name(memberName)
                    .participant_count(participantCount)
                    .build();
        });
    } 
    
    // 상세 보기
    public MeetingReservationDto getReservationById(Long meetingreservationId) {
 
        Optional<MeetingReservation> optionalReservation = meetingReservationRepository.findById(meetingreservationId);
  
        MeetingReservation reservation = optionalReservation.get();
 
        String meetingName = meetingService.getMeetingNameById(reservation.getMeetingNo());
        String memberName = memberService.getMemberNameById(reservation.getMemberNo());
        long participantCount = meetingParticipantRepository.countByMeetingReservationNo(reservation.getMeetingReservationNo());

        List<Object[]> memberInfo = memberRepository.findMemberWithDepartmentAndPosition(reservation.getMemberNo()); 
        String positionName = "직위";
        String departmentName = "부서";
        
        Object[] row = memberInfo.get(0);   
        positionName = (String) row[1];
        departmentName = (String) row[2];
         
        return MeetingReservationDto.builder()
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
                .member_name(memberName)
                .position_name(positionName)
                .department_name(departmentName)
                .participant_count(participantCount)
                .build();
    }
 
}