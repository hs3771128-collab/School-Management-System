package com.school.mamagement.service;

import com.school.mamagement.dto.request.StudentCreateRequest;
import com.school.mamagement.dto.request.StudentUpdateRequest;
import com.school.mamagement.dto.response.StudentResponse;
import com.school.mamagement.entity.Student;

import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.exception.ResourceNotFoundException;
import com.school.mamagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {


    private final StudentRepository studentRepository;
    private final UserAccountSyncService userAccountSyncService;
    private final ProfileAccountProvisioningService profileAccountProvisioningService;
    private final SystemCodeGenerator systemCodeGenerator;


// CREATE STUDENT

    public StudentResponse createStudent(StudentCreateRequest request) {

        if (request.getEmail() != null
                && studentRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already exists: " + request.getEmail()
            );
        }

        Student student = new Student();

        student.setStudentCode(systemCodeGenerator.studentCode());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setGender(request.getGender());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());

        student.setStatus(
                request.getStatus() == null
                        ? com.school.mamagement.enums.StudentStatus.ACTIVE
                        : request.getStatus()
        );

        Student savedStudent = studentRepository.save(student);

        profileAccountProvisioningService.provision(
                request.getUsername(),
                request.getPassword(),
                com.school.mamagement.enums.UserRole.STUDENT,
                toAccountStatus(savedStudent.getStatus()),
                ProfileAccountProvisioningService.ProfileType.STUDENT,
                savedStudent.getId());

        return mapToResponse(savedStudent);
    }


// GET ALL STUDENTS

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {

        return studentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


// GET STUDENT BY ID

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {

        Student student = findStudentById(id);

        return mapToResponse(student);
    }


// GET STUDENT BY STUDENT CODE

    @Transactional(readOnly = true)
    public StudentResponse getStudentByCode(String studentCode) {

        Student student = studentRepository
                .findByStudentCode(studentCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with code: " + studentCode
                        )
                );

        return mapToResponse(student);
    }


// UPDATE STUDENT

    public StudentResponse updateStudent(
            Long id,
            StudentUpdateRequest request) {

        Student student = findStudentById(id);

        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setGender(request.getGender());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());

        // EMAIL VALIDATION

        if (request.getEmail() != null
                && !request.getEmail().equals(student.getEmail())
                && studentRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already exists: " + request.getEmail()
            );
        }

        student.setEmail(request.getEmail());

        if (request.getStatus() != null) {
            student.setStatus(request.getStatus());
            userAccountSyncService.syncStudentStatus(student.getId(), request.getStatus());
        }

        Student updatedStudent = studentRepository.save(student);

        return mapToResponse(updatedStudent);
    }


// DELETE STUDENT

    public void deleteStudent(Long id) {

        Student student = findStudentById(id);

        if (userAccountSyncService.findStudentAccount(id).isPresent()) {
            throw new com.school.mamagement.exception.BusinessRuleException(
                    "Student cannot be deleted while an authentication account is linked. Deactivate the student instead.");
        }

        studentRepository.delete(student);
    }


// FIND STUDENT

    private Student findStudentById(Long id) {

        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + id
                        )
                );
    }


// ENTITY TO RESPONSE DTO

    private StudentResponse mapToResponse(Student student) {

        return StudentResponse.builder()
                .id(student.getId())
                .studentCode(student.getStudentCode())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.getGender())
                .email(student.getEmail())
                .phone(student.getPhone())
                .address(student.getAddress())
                .status(student.getStatus())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }



    private com.school.mamagement.enums.AccountStatus toAccountStatus(
            com.school.mamagement.enums.StudentStatus status) {
        return switch (status) {
            case ACTIVE -> com.school.mamagement.enums.AccountStatus.ACTIVE;
            case SUSPENDED -> com.school.mamagement.enums.AccountStatus.SUSPENDED;
            case INACTIVE, GRADUATED, TRANSFERRED -> com.school.mamagement.enums.AccountStatus.INACTIVE;
        };
    }

}
