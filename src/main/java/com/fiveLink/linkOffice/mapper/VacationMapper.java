package com.fiveLink.linkOffice.mapper;

import com.fiveLink.linkOffice.vacation.domain.VacationDto;
import com.fiveLink.linkOffice.vacation.domain.VacationTypeDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface VacationMapper {
    List<VacationDto> selectVacationList();

    int countVacation();

    int countVacationType();

    List<VacationTypeDto> selectVacationTypeList();

    int countVacationTypeList();

}
