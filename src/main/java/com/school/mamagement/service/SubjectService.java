package com.school.mamagement.service;

import com.school.mamagement.dto.request.academic.SubjectCreateRequest;
import com.school.mamagement.dto.response.academic.SubjectResponse;
import com.school.mamagement.entity.Subject;
import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.exception.ResourceNotFoundException;
import com.school.mamagement.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {
    private final SubjectRepository repository;

    public SubjectResponse create(SubjectCreateRequest r) {
        String c = clean(r.getCode()).toUpperCase(Locale.ROOT), n = clean(r.getName());
        if (repository.existsByCodeIgnoreCase(c))
            throw new DuplicateResourceException("Subject code already exists: " + c);
        if (repository.existsByNameIgnoreCase(n))
            throw new DuplicateResourceException("Subject name already exists: " + n);
        Subject e = new Subject();
        e.setCode(c);
        e.setName(n);
        e.setDescription(clean(r.getDescription()));
        e.setActive(r.getActive() == null || r.getActive());
        return map(repository.save(e));
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> all() {
        return repository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public Subject get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + id));
    }

    public SubjectResponse update(Long id, SubjectCreateRequest r) {
        Subject e = get(id);
        String c = clean(r.getCode()).toUpperCase(Locale.ROOT), n = clean(r.getName());
        if (!e.getCode().equalsIgnoreCase(c) && repository.existsByCodeIgnoreCase(c))
            throw new DuplicateResourceException("Subject code already exists: " + c);
        if (!e.getName().equalsIgnoreCase(n) && repository.existsByNameIgnoreCase(n))
            throw new DuplicateResourceException("Subject name already exists: " + n);
        e.setCode(c);
        e.setName(n);
        e.setDescription(clean(r.getDescription()));
        if (r.getActive() != null) e.setActive(r.getActive());
        return map(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }

    private String clean(String s) {
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }

    private SubjectResponse map(Subject e) {
        return SubjectResponse.builder().id(e.getId()).code(e.getCode()).name(e.getName()).description(e.getDescription()).active(e.isActive()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }
}
