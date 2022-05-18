package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByReporterId(Long id);
}
