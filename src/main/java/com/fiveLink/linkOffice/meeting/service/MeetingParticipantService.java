package com.fiveLink.linkOffice.meeting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fiveLink.linkOffice.meeting.domain.MeetingParticipantDto;
import com.fiveLink.linkOffice.meeting.domain.MeetingReservation;
import com.fiveLink.linkOffice.meeting.domain.MeetingReservationDto;
import com.fiveLink.linkOffice.meeting.repository.MeetingParticipantRepository;
import com.fiveLink.linkOffice.meeting.repository.MeetingReservationRepository;

@Service
public class MeetingParticipantService {
    private final MeetingReservationRepository meetingReservationRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;

    @Autowired
    public MeetingParticipantService(MeetingReservationRepository meetingReservationRepository,
                                     MeetingParticipantRepository meetingParticipantRepository) {
        this.meetingReservationRepository = meetingReservationRepository;
        this.meetingParticipantRepository = meetingParticipantRepository;
    }

    @Transactional
    public void saveReservationAndParticipants(MeetingReservationDto reservationDto, List<MeetingParticipantDto> participants) {
        MeetingReservation savedReservation = meetingReservationRepository.save(reservationDto.toEntity());

        // 참가자 정보 저장
        participants.forEach(participantDto -> {
            participantDto.setMeeting_reservation_no(savedReservation.getMeetingReservationNo());
            meetingParticipantRepository.save(participantDto.toEntity());  
        });
    }
}
