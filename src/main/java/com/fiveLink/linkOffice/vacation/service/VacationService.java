package com.fiveLink.linkOffice.vacation.service;


import com.fiveLink.linkOffice.vacation.domain.Vacation;
import com.fiveLink.linkOffice.vacation.domain.VacationDto;
import com.fiveLink.linkOffice.mapper.VacationMapper;
import com.fiveLink.linkOffice.vacation.domain.VacationType;
import com.fiveLink.linkOffice.vacation.domain.VacationTypeDto;
import com.fiveLink.linkOffice.vacation.repository.VacationRepository;
import com.fiveLink.linkOffice.vacation.repository.VacationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VacationService {

    private final VacationRepository vacationRepository;
    private final VacationTypeRepository vacationTypeRepository;
    private final VacationMapper vacationMapper;

    @Autowired
    public VacationService(VacationRepository vacationRepository, VacationMapper vacationMapper, VacationTypeRepository vacationTypeRepository){
        this.vacationRepository = vacationRepository;
        this.vacationMapper = vacationMapper;
        this.vacationTypeRepository = vacationTypeRepository;
    }

    public int addVacation(VacationDto dto) {
        int result = -1;
        try {
            Vacation vacation = dto.toEntity();
            vacationRepository.save(vacation);

            result = 1;

        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public int addTypeVacation(VacationTypeDto dto) {
        int result = -1;
        try {
            VacationType vacationTypes = dto.toEntity();
            vacationTypeRepository.save(vacationTypes);

            result = 1;

        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public List<VacationDto> selectVacationList(){
        return vacationMapper.selectVacationList();
    }

    public int countVacation() {
        return vacationMapper.countVacation();


    }

    public Vacation findVacationById(Long vacationId) {
        return vacationRepository.findById(vacationId).orElse(null);
    }

    public int updateVacation(Vacation vacation) {
        try {
            vacationRepository.save(vacation); // JPA의 save는 수정도 처리합니다.
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}