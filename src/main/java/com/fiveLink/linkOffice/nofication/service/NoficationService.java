package com.fiveLink.linkOffice.nofication.service;

import com.fiveLink.linkOffice.nofication.domain.Nofication;
import com.fiveLink.linkOffice.nofication.domain.NoficationDto;
import com.fiveLink.linkOffice.nofication.respository.NoficationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoficationService {
    private final NoficationRepository noficationRepository;

    @Autowired
    public NoficationService(NoficationRepository noficationRepository){
        this.noficationRepository = noficationRepository;
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
}
