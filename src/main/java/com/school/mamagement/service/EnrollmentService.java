package com.school.mamagement.service;

import com.school.mamagement.dto.request.academic.EnrollmentCreateRequest;
import com.school.mamagement.dto.response.academic.EnrollmentResponse;
import com.school.mamagement.entity.*;
import com.school.mamagement.enums.*;
import com.school.mamagement.exception.*;
import com.school.mamagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {
    private final EnrollmentRepository repository;
    private final StudentRepository students;
    private final AcademicYearRepository years;
    private final SchoolClassRepository classes;
    private final SectionRepository sections;

    public EnrollmentResponse create(EnrollmentCreateRequest r) {
        Student s = student(r.getStudentId());
        if (s.getStatus() != StudentStatus.ACTIVE)
            throw new BusinessRuleException("Only ACTIVE students can be enrolled");
        AcademicYear y = year(r.getAcademicYearId());
        SchoolClass c = clazz(r.getSchoolClassId());
        Section sec = section(r.getSectionId());
        validatePlacement(y, c, sec);
        if (repository.existsByStudentIdAndAcademicYearId(s.getId(), y.getId()))
            throw new DuplicateResourceException("Student is already enrolled for this academic year");
        if (repository.countBySectionIdAndStatus(sec.getId(), EnrollmentStatus.ACTIVE) >= sec.getCapacity())
            throw new BusinessRuleException("Section capacity has been reached");
        Enrollment e = new Enrollment();
        e.setStudent(s);
        e.setAcademicYear(y);
        e.setSchoolClass(c);
        e.setSection(sec);
        e.setStatus(r.getStatus() == null ? EnrollmentStatus.ACTIVE : r.getStatus());
        e.setEnrolledAt(LocalDate.now());
        return map(repository.save(e));
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> all() {
        return repository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> byStudent(Long id) {
        student(id);
        return repository.findByStudentId(id).stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public Enrollment get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + id));
    }

    public EnrollmentResponse update(Long id, EnrollmentCreateRequest r) {
        Enrollment e = get(id);
        if (!e.getStudent().getId().equals(r.getStudentId()) || !e.getAcademicYear().getId().equals(r.getAcademicYearId()))
            throw new BusinessRuleException("Student and academic year cannot be changed on an enrollment; create a new enrollment instead");
        if (r.getStatus() != null && r.getStatus() == EnrollmentStatus.ACTIVE && e.getStatus() != EnrollmentStatus.ACTIVE && repository.countBySectionIdAndStatus(e.getSection().getId(), EnrollmentStatus.ACTIVE) >= e.getSection().getCapacity())
            throw new BusinessRuleException("Section capacity has been reached");
        if (r.getStatus() != null) e.setStatus(r.getStatus());
        return map(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(get(id));
    }

    private void validatePlacement(AcademicYear y, SchoolClass c, Section sec) {
        if (sec.getSchoolClass() == null || !sec.getSchoolClass().getId().equals(c.getId()))
            throw new BusinessRuleException("Section does not belong to the selected class");
        if (!c.isActive() || !sec.isActive()) throw new BusinessRuleException("Class and section must be active");
        if (y.getStatus() != AcademicYearStatus.ACTIVE)
            throw new BusinessRuleException("Enrollment is allowed only in an ACTIVE academic year");
    }

    private Student student(Long id) {
        return students.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }

    private AcademicYear year(Long id) {
        return years.findById(id).orElseThrow(() -> new ResourceNotFoundException("Academic year not found: " + id));
    }

    private SchoolClass clazz(Long id) {
        return classes.findById(id).orElseThrow(() -> new ResourceNotFoundException("Class not found: " + id));
    }

    private Section section(Long id) {
        return sections.findById(id).orElseThrow(() -> new ResourceNotFoundException("Section not found: " + id));
    }

    private EnrollmentResponse map(Enrollment e) {
        return EnrollmentResponse.builder().id(e.getId()).studentId(e.getStudent().getId()).studentCode(e.getStudent().getStudentCode()).academicYearId(e.getAcademicYear().getId()).academicYearName(e.getAcademicYear().getName()).schoolClassId(e.getSchoolClass().getId()).schoolClassName(e.getSchoolClass().getName()).sectionId(e.getSection().getId()).sectionName(e.getSection().getName()).status(e.getStatus()).enrolledAt(e.getEnrolledAt()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }
}
