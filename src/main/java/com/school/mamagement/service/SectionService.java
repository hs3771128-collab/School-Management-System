package com.school.mamagement.service;

import com.school.mamagement.dto.request.academic.SectionCreateRequest;
import com.school.mamagement.dto.response.academic.SectionResponse;
import com.school.mamagement.entity.SchoolClass;
import com.school.mamagement.entity.Section;
import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.exception.ResourceNotFoundException;
import com.school.mamagement.repository.SchoolClassRepository;
import com.school.mamagement.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionService {
    private final SectionRepository repository;
    private final SchoolClassRepository classRepository;

    public SectionResponse create(SectionCreateRequest r) {
        SchoolClass c = getClass(r.getSchoolClassId());
        validateCapacity(r.getCapacity());
        String n = clean(r.getName());
        if (repository.existsBySchoolClassIdAndNameIgnoreCase(c.getId(), n))
            throw new DuplicateResourceException("Section already exists in class: " + n);
        Section e = new Section();
        e.setName(n);
        e.setSchoolClass(c);
        e.setCapacity(r.getCapacity());
        e.setActive(r.getActive() == null || r.getActive());
        return map(repository.save(e));
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> all() {
        return repository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public Section get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Section not found: " + id));
    }

    public SectionResponse update(Long id, SectionCreateRequest r) {
        Section e = get(id);
        SchoolClass c = getClass(r.getSchoolClassId());
        validateCapacity(r.getCapacity());
        String n = clean(r.getName());
        if ((!e.getName().equalsIgnoreCase(n) || !e.getSchoolClass().getId().equals(c.getId())) && repository.existsBySchoolClassIdAndNameIgnoreCase(c.getId(), n))
            throw new DuplicateResourceException("Section already exists in class: " + n);
        e.setName(n);
        e.setSchoolClass(c);
        e.setCapacity(r.getCapacity());
        if (r.getActive() != null) e.setActive(r.getActive());
        return map(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }

    private SchoolClass getClass(Long id) {
        return classRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Class not found: " + id));
    }

    private void validateCapacity(Integer c) {
        if (c == null || c < 1 || c > 500)
            throw new IllegalArgumentException("Section capacity must be between 1 and 500");
    }

    private String clean(String s) {
        return s == null ? null : s.trim();
    }

    private SectionResponse map(Section e) {
        return SectionResponse.builder().id(e.getId()).name(e.getName()).schoolClassId(e.getSchoolClass().getId()).schoolClassName(e.getSchoolClass().getName()).capacity(e.getCapacity()).active(e.isActive()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }
}
