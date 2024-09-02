package com.fiveLink.linkOffice.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiveLink.linkOffice.organization.domain.Position;

public interface PositionRepository extends JpaRepository<Position, Long> {

}