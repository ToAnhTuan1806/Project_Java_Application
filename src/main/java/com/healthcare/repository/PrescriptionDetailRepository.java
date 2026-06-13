package com.healthcare.repository;

import com.healthcare.entity.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, Long> {
}