package com.gatekeeper.api.repository;

import com.gatekeeper.api.model.ScanReport;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory implementation of ScanReportRepository
 */
@Repository
public class InMemoryScanReportRepository implements ScanReportRepository {

    private final List<ScanReport> reports = new CopyOnWriteArrayList<>();

    @Override
    public void save(ScanReport report) {
        // Add at the beginning for most recent first
        reports.addFirst(report);
    }

    @Override
    public List<ScanReport> findAll() {
        return new ArrayList<>(reports);
    }

    @Override
    public Optional<ScanReport> findById(String id) {
        return reports.stream()
                .filter(report -> report.id().equals(id))
                .findFirst();
    }

    @Override
    public List<ScanReport> findByRepository(String repository) {
        return reports.stream()
                .filter(report -> report.repository().equals(repository))
                .toList();
    }

    @Override
    public void deleteById(String id) {
        reports.removeIf(report -> report.id().equals(id));
    }

    @Override
    public void deleteAll() {
        reports.clear();
    }

    @Override
    public long count() {
        return reports.size();
    }
}
