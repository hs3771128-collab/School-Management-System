package com.school.mamagement.service;

import com.school.mamagement.dto.request.academic.SchoolClassCreateRequest;
import com.school.mamagement.dto.response.academic.SchoolClassResponse;
import com.school.mamagement.entity.SchoolClass;
import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.exception.ResourceNotFoundException;
import com.school.mamagement.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SchoolClassService {
    private final SchoolClassRepository repository;

    public SchoolClassResponse create(SchoolClassCreateRequest r) {
        String name = clean(r.getName());
        if (repository.existsByNameIgnoreCase(name))
            throw new DuplicateResourceException("Class already exists: " + name);
        validateGrade(r.getGradeLevel());
        SchoolClass e = new SchoolClass();
        e.setName(name);
        e.setGradeLevel(r.getGradeLevel());
        e.setActive(r.getActive() == null || r.getActive());
        return map(repository.save(e));
    }

    @Transactional(readOnly = true)
    public List<SchoolClassResponse> all() {
        return repository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public SchoolClass get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Class not found: " + id));
    }

    public SchoolClassResponse update(Long id, SchoolClassCreateRequest r) {
        SchoolClass e = get(id);
        String n = clean(r.getName());
        if (!e.getName().equalsIgnoreCase(n) && repository.existsByNameIgnoreCase(n))
            throw new DuplicateResourceException("Class already exists: " + n);
        validateGrade(r.getGradeLevel());
        e.setName(n);
        e.setGradeLevel(r.getGradeLevel());
        if (r.getActive() != null) e.setActive(r.getActive());
        return map(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }

    private void validateGrade(Integer g) {
        if (g == null || g < 1 || g > 12) throw new IllegalArgumentException("Grade level must be between 1 and 12");
    }

    private String clean(String s) {
        return s == null ? null : s.trim();
    }

    private SchoolClassResponse map(SchoolClass e) {
        return SchoolClassResponse.builder().id(e.getId()).name(e.getName()).gradeLevel(e.getGradeLevel()).active(e.isActive()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }
}
