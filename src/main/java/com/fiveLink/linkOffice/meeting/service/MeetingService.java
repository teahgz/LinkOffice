package com.fiveLink.linkOffice.meeting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	    List<Meeting> meetings = meetingRepository.findAll();
	    List<MeetingDto> meetingDtos = new ArrayList();

	    for (Meeting meeting : meetings) {
	        MeetingDto meetingDto = MeetingDto.fromEntity(meeting);
	        meetingDtos.add(meetingDto);
	    } 
	    return meetingDtos;
	} 
}
