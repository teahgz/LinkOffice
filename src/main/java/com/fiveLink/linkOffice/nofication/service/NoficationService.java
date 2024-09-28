package com.fiveLink.linkOffice.nofication.service;

import com.fiveLink.linkOffice.mapper.NoficationMapper;
import com.fiveLink.linkOffice.nofication.domain.Nofication;
import com.fiveLink.linkOffice.nofication.domain.NoficationDto;
import com.fiveLink.linkOffice.nofication.respository.NoficationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoficationService {
    private final NoficationRepository noficationRepository;
    private final NoficationMapper noficationMapper;

    @Autowired
    public NoficationService(NoficationRepository noficationRepository, NoficationMapper noficationMapper){
        this.noficationRepository = noficationRepository;
        this.noficationMapper = noficationMapper;
    }

    public int insertAlarm(NoficationDto noficationDto) {
        int result = -1;
        try{
            Nofication nofication = noficationDto.toEntity();
            noficationRepository.save(nofication);
            result = 1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    //현재 사용자의 안읽음 개수
    public int bellCount(Long memberNo) {
        int result = -1;
        try{
            result = noficationMapper.bellCount(memberNo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    //현재 사용자의 안읽음 알람 리스트
    public List<NoficationDto> selectUnreadList(Long memberNo) {
        return noficationMapper.selectUnreadList(memberNo);
    }
}
