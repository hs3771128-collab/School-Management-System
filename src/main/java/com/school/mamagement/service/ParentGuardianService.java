package com.school.mamagement.service;

import com.school.mamagement.dto.request.ParentGuardianCreateRequest;
import com.school.mamagement.dto.request.ParentGuardianUpdateRequest;
import com.school.mamagement.dto.response.ParentGuardianResponse;
import com.school.mamagement.entity.ParentGuardian;
import com.school.mamagement.entity.Student;
import com.school.mamagement.exception.ResourceNotFoundException;
import com.school.mamagement.repository.ParentGuardianRepository;
import com.school.mamagement.repository.StudentRepository;
import com.school.mamagement.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParentGuardianService {

    private final ParentGuardianRepository parentGuardianRepository;
    private final StudentRepository studentRepository;
    private final UserAccountRepository userAccountRepository;
    private final ProfileAccountProvisioningService profileAccountProvisioningService;

    public ParentGuardianResponse createParentGuardian(ParentGuardianCreateRequest request) {
        String studentCode = request.getStudentCode().trim();

        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with code: " + studentCode));

        ParentGuardian parentGuardian = new ParentGuardian();
        parentGuardian.setStudent(student);
        parentGuardian.setFirstName(request.getFirstName().trim());
        parentGuardian.setLastName(request.getLastName().trim());
        parentGuardian.setEmail(normalizeOptional(request.getEmail()));
        parentGuardian.setPhone(request.getPhone().trim());
        parentGuardian.setAddress(normalizeOptional(request.getAddress()));
        parentGuardian.setCnic(normalizeOptional(request.getCnic()));
        parentGuardian.setOccupation(normalizeOptional(request.getOccupation()));
        parentGuardian.setRelationshipType(request.getRelationshipType());

        ParentGuardian savedParentGuardian = parentGuardianRepository.save(parentGuardian);

        profileAccountProvisioningService.provision(
                request.getUsername(),
                request.getPassword(),
                com.school.mamagement.enums.UserRole.PARENT_GUARDIAN,
                com.school.mamagement.enums.AccountStatus.ACTIVE,
                ProfileAccountProvisioningService.ProfileType.PARENT_GUARDIAN,
                savedParentGuardian.getId());

        return mapToResponse(savedParentGuardian);
    }

    @Transactional(readOnly = true)
    public List<ParentGuardianResponse> getAllParentGuardians() {
        return parentGuardianRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ParentGuardianResponse getParentGuardianById(Long id) {
        return mapToResponse(findParentGuardianById(id));
    }

    @Transactional(readOnly = true)
    public List<ParentGuardianResponse> getParentGuardiansByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        List<ParentGuardian> parentGuardians = parentGuardianRepository.findByStudentId(studentId);
        return parentGuardians.stream().map(this::mapToResponse).toList();
    }

    public ParentGuardianResponse updateParentGuardian(
            Long id,
            ParentGuardianUpdateRequest request) {

        ParentGuardian parentGuardian = findParentGuardianById(id);

        parentGuardian.setFirstName(request.getFirstName().trim());
        parentGuardian.setLastName(request.getLastName().trim());
        parentGuardian.setEmail(normalizeOptional(request.getEmail()));
        parentGuardian.setPhone(request.getPhone().trim());
        parentGuardian.setAddress(normalizeOptional(request.getAddress()));
        parentGuardian.setCnic(normalizeOptional(request.getCnic()));
        parentGuardian.setOccupation(normalizeOptional(request.getOccupation()));
        parentGuardian.setRelationshipType(request.getRelationshipType());

        return mapToResponse(parentGuardianRepository.save(parentGuardian));
    }

    public void deleteParentGuardian(Long id) {
        ParentGuardian parentGuardian = findParentGuardianById(id);
        if (userAccountRepository.existsByParentGuardianId(id)) {
            throw new com.school.mamagement.exception.BusinessRuleException(
                    "Parent/Guardian cannot be deleted while an authentication account is linked.");
        }
        parentGuardianRepository.delete(parentGuardian);
    }

    private ParentGuardian findParentGuardianById(Long id) {
        return parentGuardianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Parent/Guardian not found with id: " + id));
    }

    private ParentGuardianResponse mapToResponse(ParentGuardian parentGuardian) {
        Student student = parentGuardian.getStudent();

        String studentName = (student.getFirstName() + " " + student.getLastName()).trim();

        return ParentGuardianResponse.builder()
                .id(parentGuardian.getId())
                .studentId(student.getId())
                .studentCode(student.getStudentCode())
                .studentName(studentName)
                .firstName(parentGuardian.getFirstName())
                .lastName(parentGuardian.getLastName())
                .email(parentGuardian.getEmail())
                .phone(parentGuardian.getPhone())
                .address(parentGuardian.getAddress())
                .cnic(parentGuardian.getCnic())
                .occupation(parentGuardian.getOccupation())
                .relationshipType(parentGuardian.getRelationshipType())
                .createdAt(parentGuardian.getCreatedAt())
                .updatedAt(parentGuardian.getUpdatedAt())
                .build();
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
