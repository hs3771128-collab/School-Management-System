package com.school.mamagement.service;

import com.school.mamagement.dto.request.academic.AcademicYearCreateRequest;
import com.school.mamagement.dto.response.academic.AcademicYearResponse;
import com.school.mamagement.entity.AcademicYear;
import com.school.mamagement.enums.AcademicYearStatus;
import com.school.mamagement.exception.BusinessRuleException;
import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.exception.ResourceNotFoundException;
import com.school.mamagement.repository.AcademicYearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AcademicYearService {
    private final AcademicYearRepository repository;

    public AcademicYearResponse create(AcademicYearCreateRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());
        String name = clean(request.getName());
        if (repository.existsByNameIgnoreCase(name))
            throw new DuplicateResourceException("Academic year already exists: " + name);
        AcademicYearStatus status = request.getStatus() == null ? AcademicYearStatus.PLANNED : request.getStatus();
        validateActive(status, null);
        AcademicYear entity = new AcademicYear();
        entity.setName(name);
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setStatus(status);
        return map(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<AcademicYearResponse> all() {
        return repository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public AcademicYear get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Academic year not found: " + id));
    }

    public AcademicYearResponse update(Long id, AcademicYearCreateRequest request) {
        AcademicYear entity = repository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Academic year not found: " + id));
        validateDates(request.getStartDate(), request.getEndDate());
        String name = clean(request.getName());
        if (!entity.getName().equalsIgnoreCase(name) && repository.existsByNameIgnoreCase(name))
            throw new DuplicateResourceException("Academic year already exists: " + name);
        if (request.getStatus() != null) validateActive(request.getStatus(), entity.getId());
        entity.setName(name);
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        if (request.getStatus() != null) entity.setStatus(request.getStatus());
        return map(repository.save(entity));
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (!end.isAfter(start)) throw new BusinessRuleException("End date must be after start date");
    }

    private void validateActive(AcademicYearStatus status, Long currentId) {
        if (status == AcademicYearStatus.ACTIVE) {
            var activeYears = repository.findActiveForUpdate();
            boolean anotherActiveYearExists = activeYears.stream().anyMatch(y -> !y.getId().equals(currentId));
            if (anotherActiveYearExists) throw new BusinessRuleException("Only one academic year can be ACTIVE");
        }
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    private AcademicYearResponse map(AcademicYear e) {
        return AcademicYearResponse.builder().id(e.getId()).name(e.getName()).startDate(e.getStartDate()).endDate(e.getEndDate()).status(e.getStatus()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }
}
