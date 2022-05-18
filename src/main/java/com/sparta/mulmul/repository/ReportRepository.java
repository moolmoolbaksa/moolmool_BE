package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByReporterIdAndReportedItemId(Long id, Long itemid);
    Optional<Report> findByReporterIdAndReportedUserId(Long id, Long userId);
}
