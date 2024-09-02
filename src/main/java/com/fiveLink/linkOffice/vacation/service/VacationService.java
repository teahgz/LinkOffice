package com.fiveLink.linkOffice.vacation.service;


import com.fiveLink.linkOffice.vacation.domain.Vacation;
import com.fiveLink.linkOffice.vacation.domain.VacationDto;
import com.fiveLink.linkOffice.vacation.repository.VacationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VacationService {
    private final VacationRepository vacationRepository;

    @Autowired
    public VacationService(VacationRepository vacationRepository){
        this.vacationRepository = vacationRepository;
    }

    public int addVacation(VacationDto dto){
        int result = -1;
        try {
            Vacation vacation = dto.toEntity();
            vacationRepository.save(vacation);
            result = 1;
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

}
