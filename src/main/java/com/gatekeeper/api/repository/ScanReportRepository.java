package com.gatekeeper.api.repository;

import com.gatekeeper.api.model.ScanReport;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for scan reports
 */
public interface ScanReportRepository {

    void save(ScanReport report);

    List<ScanReport> findAll();

    Optional<ScanReport> findById(String id);

    List<ScanReport> findByRepository(String repository);

    void deleteById(String id);

    void deleteAll();

    long count();
}
