package com.school.mamagement.service;

import com.school.mamagement.dto.request.TeacherCreateRequest;
import com.school.mamagement.dto.request.TeacherUpdateRequest;
import com.school.mamagement.dto.response.TeacherResponse;
import com.school.mamagement.entity.Teacher;

import com.school.mamagement.repository.TeacherRepository;
import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {


    private final TeacherRepository teacherRepository;
    private final UserAccountSyncService userAccountSyncService;
    private final ProfileAccountProvisioningService profileAccountProvisioningService;
    private final SystemCodeGenerator systemCodeGenerator;


// CREATE TEACHER

    public TeacherResponse createTeacher(TeacherCreateRequest request) {

        if (request.getEmail() != null
                && teacherRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already exists: " + request.getEmail()
            );
        }

        Teacher teacher = new Teacher();

        teacher.setEmployeeCode(systemCodeGenerator.teacherCode());
        teacher.setFirstName(request.getFirstName());
        teacher.setLastName(request.getLastName());
        teacher.setDateOfBirth(request.getDateOfBirth());
        teacher.setGender(request.getGender());
        teacher.setEmail(request.getEmail());
        teacher.setPhone(request.getPhone());
        teacher.setAddress(request.getAddress());
        teacher.setQualification(request.getQualification());
        teacher.setJoiningDate(request.getJoiningDate());

        if (request.getStatus() != null) {
            teacher.setStatus(request.getStatus());
        }

        Teacher savedTeacher = teacherRepository.save(teacher);

        profileAccountProvisioningService.provision(
                request.getUsername(),
                request.getPassword(),
                com.school.mamagement.enums.UserRole.TEACHER,
                toAccountStatus(savedTeacher.getStatus()),
                ProfileAccountProvisioningService.ProfileType.TEACHER,
                savedTeacher.getId());

        return mapToResponse(savedTeacher);
    }


// GET ALL TEACHERS

    @Transactional(readOnly = true)
    public List<TeacherResponse> getAllTeachers() {

        return teacherRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


// GET TEACHER BY ID

    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long id) {

        Teacher teacher = findTeacherById(id);

        return mapToResponse(teacher);
    }


// GET TEACHER BY EMPLOYEE CODE

    @Transactional(readOnly = true)
    public TeacherResponse getTeacherByEmployeeCode(
            String employeeCode) {

        Teacher teacher = teacherRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher not found with employee code: "
                                        + employeeCode
                        )
                );

        return mapToResponse(teacher);
    }


// UPDATE TEACHER

    public TeacherResponse updateTeacher(
            Long id,
            TeacherUpdateRequest request) {

        Teacher teacher = findTeacherById(id);

        teacher.setFirstName(request.getFirstName());
        teacher.setLastName(request.getLastName());
        teacher.setDateOfBirth(request.getDateOfBirth());
        teacher.setGender(request.getGender());
        teacher.setPhone(request.getPhone());
        teacher.setAddress(request.getAddress());
        teacher.setQualification(request.getQualification());
        teacher.setJoiningDate(request.getJoiningDate());

        // EMAIL VALIDATION

        if (request.getEmail() != null
                && !request.getEmail().equals(teacher.getEmail())
                && teacherRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already exists: " + request.getEmail()
            );
        }

        teacher.setEmail(request.getEmail());

        if (request.getStatus() != null) {
            teacher.setStatus(request.getStatus());
            userAccountSyncService.syncTeacherStatus(teacher.getId(), request.getStatus());
        }

        Teacher updatedTeacher = teacherRepository.save(teacher);

        return mapToResponse(updatedTeacher);
    }


// DELETE TEACHER

    public void deleteTeacher(Long id) {

        Teacher teacher = findTeacherById(id);

        if (userAccountSyncService.findTeacherAccount(id).isPresent()) {
            throw new com.school.mamagement.exception.BusinessRuleException(
                    "Teacher cannot be deleted while an authentication account is linked. Deactivate the teacher instead.");
        }

        teacherRepository.delete(teacher);
    }


// FIND TEACHER

    private Teacher findTeacherById(Long id) {

        return teacherRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher not found with id: " + id
                        )
                );
    }


// ENTITY TO RESPONSE DTO

    private TeacherResponse mapToResponse(Teacher teacher) {

        return TeacherResponse.builder()
                .id(teacher.getId())
                .employeeCode(teacher.getEmployeeCode())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .dateOfBirth(teacher.getDateOfBirth())
                .gender(teacher.getGender())
                .email(teacher.getEmail())
                .phone(teacher.getPhone())
                .address(teacher.getAddress())
                .qualification(teacher.getQualification())
                .joiningDate(teacher.getJoiningDate())
                .status(teacher.getStatus())
                .createdAt(teacher.getCreatedAt())
                .updatedAt(teacher.getUpdatedAt())
                .build();
    }



    private com.school.mamagement.enums.AccountStatus toAccountStatus(
            com.school.mamagement.enums.TeacherStatus status) {
        return switch (status) {
            case ACTIVE -> com.school.mamagement.enums.AccountStatus.ACTIVE;
            case ON_LEAVE -> com.school.mamagement.enums.AccountStatus.SUSPENDED;
            case INACTIVE, TERMINATED -> com.school.mamagement.enums.AccountStatus.INACTIVE;
        };
    }

}
