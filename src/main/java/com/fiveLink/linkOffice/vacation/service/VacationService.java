package com.fiveLink.linkOffice.vacation.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.mapper.VacationMapper;
import com.fiveLink.linkOffice.vacation.domain.Vacation;
import com.fiveLink.linkOffice.vacation.domain.VacationDto;
import com.fiveLink.linkOffice.vacation.domain.VacationOneUnder;
import com.fiveLink.linkOffice.vacation.domain.VacationOneUnderDto;
import com.fiveLink.linkOffice.vacation.domain.VacationType;
import com.fiveLink.linkOffice.vacation.domain.VacationTypeDto;
import com.fiveLink.linkOffice.vacation.repository.VacationCheckRepository;
import com.fiveLink.linkOffice.vacation.repository.VacationRepository;
import com.fiveLink.linkOffice.vacation.repository.VacationStandardRepository;
import com.fiveLink.linkOffice.vacation.repository.VacationTypeRepository;

@Service
public class VacationService {

    private final VacationRepository vacationRepository;
    private final VacationTypeRepository vacationTypeRepository;
    private final VacationMapper vacationMapper;
    private final VacationCheckRepository vacationCheckRepository;
    private final VacationStandardRepository vacationStandardRepository;

    @Autowired
    public VacationService(VacationRepository vacationRepository, VacationMapper vacationMapper, VacationTypeRepository vacationTypeRepository, VacationCheckRepository vacationCheckRepository ,VacationStandardRepository vacationStandardRepository){
        this.vacationRepository = vacationRepository;
        this.vacationMapper = vacationMapper;
        this.vacationTypeRepository = vacationTypeRepository;
        this.vacationCheckRepository = vacationCheckRepository;
        this.vacationStandardRepository =vacationStandardRepository;
    }
//휴가 연차 생성
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


    public List<VacationDto> selectVacationList(){
        return vacationMapper.selectVacationList();
    }

    public int countVacation() {
        return vacationMapper.countVacation();


    }
// 휴가 종류 생성

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
    public int countVacationType() {
        return vacationMapper.countVacationType();


    }
    public List<VacationTypeDto> selectVacationTypeList(){
        return vacationMapper.selectVacationTypeList();
    }

    public VacationTypeDto getVacationTypeByNo(Long vacationTypeNo) {
        return vacationTypeRepository.findByVacationTypeNo(vacationTypeNo)
                .orElseThrow(() -> new RuntimeException("휴가 타입을 찾을 수 없습니다."));
    }

    //1년미만
    public int checkOneYear(VacationOneUnderDto dto) {
        int result = -1;
        try {
            VacationOneUnder vacationOneUnder = dto.toEntity();
            vacationCheckRepository.save(vacationOneUnder);

            result = 1;

        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public int countCheckOneYear() {
        return vacationMapper.countCheckOneYear();


    }

    //휴가 지급 기준
    public int checkStandard(VacationStandardDto dto) {
        int result = -1;
        try {
            VacationStandard vacationStandard = dto.toEntity();
            vacationStandardRepository.save(vacationStandard);
            result = 1;

        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public int countStandard() {
        return vacationMapper.countStandard();


    }
    public List<VacationStandardDto> selectVacationStandard(){
        return vacationMapper.selectVacationStandard();
    }


}