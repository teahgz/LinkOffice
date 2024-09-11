package com.fiveLink.linkOffice.meeting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fiveLink.linkOffice.meeting.domain.Meeting;
import com.fiveLink.linkOffice.meeting.domain.MeetingDto;
import com.fiveLink.linkOffice.meeting.repository.MeetingRepository; 

@Service
public class MeetingService {

	private final MeetingRepository meetingRepository;  
	
	@Autowired
	public MeetingService (MeetingRepository meetingRepository) {
		this.meetingRepository = meetingRepository;  
	}
	
	public List<MeetingDto> getAllMeetings() {
	    List<Meeting> meetings = meetingRepository.findByMeetingStatus(0L);
	    List<MeetingDto> meetingDtos = new ArrayList();

	    for (Meeting meeting : meetings) {
	        MeetingDto meetingDto = MeetingDto.toDto(meeting);
	        meetingDtos.add(meetingDto);
	    } 
	    return meetingDtos;
	} 
	
	public MeetingDto getMeetingById(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .map(MeetingDto::toDto)
                .orElse(null);
    }

	 
	// 등록
	public boolean isMeetingNameExists(String meetingName) {
		 return meetingRepository.existsByMeetingNameAndMeetingStatus(meetingName, 0L);
	}
 
	 
	@Transactional
	public Meeting saveMeeting(MeetingDto meetingDto) {
	    Meeting meeting = meetingDto.toEntity();
	    return meetingRepository.save(meeting);
	}
	  
}
