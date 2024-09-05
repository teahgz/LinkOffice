package com.fiveLink.linkOffice.vacation.repository;

import com.fiveLink.linkOffice.vacation.domain.VacationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationTypeRepository extends JpaRepository<VacationType, Long> {

}
