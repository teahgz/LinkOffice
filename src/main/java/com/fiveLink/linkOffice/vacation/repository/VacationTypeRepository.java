package com.fiveLink.linkOffice.vacation.repository;

import com.fiveLink.linkOffice.vacation.domain.VacationType;
import com.fiveLink.linkOffice.vacation.domain.VacationTypeDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VacationTypeRepository extends JpaRepository<VacationType, Long> {
    Optional<VacationTypeDto> findByVacationTypeNo(Long vacationTypeNo);
}
