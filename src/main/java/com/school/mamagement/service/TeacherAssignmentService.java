package com.school.mamagement.service;

import com.school.mamagement.dto.request.academic.TeacherAssignmentCreateRequest;
import com.school.mamagement.dto.response.academic.TeacherAssignmentResponse;
import com.school.mamagement.entity.AcademicYear;
import com.school.mamagement.entity.Section;
import com.school.mamagement.entity.Subject;
import com.school.mamagement.entity.Teacher;
import com.school.mamagement.entity.TeacherAssignment;
import com.school.mamagement.enums.AcademicYearStatus;
import com.school.mamagement.enums.TeacherStatus;
import com.school.mamagement.exception.BusinessRuleException;
import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.exception.ResourceNotFoundException;
import com.school.mamagement.repository.AcademicYearRepository;
import com.school.mamagement.repository.SectionRepository;
import com.school.mamagement.repository.SubjectRepository;
import com.school.mamagement.repository.TeacherAssignmentRepository;
import com.school.mamagement.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherAssignmentService {

    private final TeacherAssignmentRepository assignmentRepository;
    private final TeacherRepository teacherRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;

    public TeacherAssignmentResponse create(
            TeacherAssignmentCreateRequest request
    ) {

        Teacher teacher = getTeacher(request.getTeacherId());
        AcademicYear academicYear = getAcademicYear(request.getAcademicYearId());
        Section section = getSection(request.getSectionId());
        Subject subject = getSubject(request.getSubjectId());

        validateTeacher(teacher);
        validateAcademicYear(academicYear);
        validateSection(section);
        validateSubject(subject);

        validateSectionBelongsToAcademicYear(
                section,
                academicYear
        );

        if (assignmentRepository
                .existsByTeacherIdAndAcademicYearIdAndSectionIdAndSubjectId(
                        teacher.getId(),
                        academicYear.getId(),
                        section.getId(),
                        subject.getId()
                )) {

            throw new DuplicateResourceException(
                    "Teacher is already assigned to this subject and section for the academic year"
            );
        }

        TeacherAssignment assignment = new TeacherAssignment();

        assignment.setTeacher(teacher);
        assignment.setAcademicYear(academicYear);
        assignment.setSection(section);
        assignment.setSubject(subject);

        TeacherAssignment saved =
                assignmentRepository.save(assignment);

        return map(saved);
    }

    @Transactional(readOnly = true)
    public List<TeacherAssignmentResponse> getAll() {

        return assignmentRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeacherAssignmentResponse getById(Long id) {

        return map(getAssignment(id));
    }

    @Transactional(readOnly = true)
    public List<TeacherAssignmentResponse> getByTeacherAndAcademicYear(
            Long teacherId,
            Long academicYearId
    ) {

        getTeacher(teacherId);
        getAcademicYear(academicYearId);

        return assignmentRepository
                .findByTeacherIdAndAcademicYearId(
                        teacherId,
                        academicYearId
                )
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TeacherAssignmentResponse> getBySectionAndAcademicYear(
            Long sectionId,
            Long academicYearId
    ) {

        getSection(sectionId);
        getAcademicYear(academicYearId);

        return assignmentRepository
                .findBySectionIdAndAcademicYearId(
                        sectionId,
                        academicYearId
                )
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TeacherAssignmentResponse> getByAcademicYear(
            Long academicYearId
    ) {

        getAcademicYear(academicYearId);

        return assignmentRepository
                .findByAcademicYearId(academicYearId)
                .stream()
                .map(this::map)
                .toList();
    }

    public TeacherAssignmentResponse update(
            Long id,
            TeacherAssignmentCreateRequest request
    ) {

        TeacherAssignment assignment =
                assignmentRepository.findByIdForUpdate(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Teacher assignment not found: " + id
                                )
                        );

        Teacher teacher = getTeacher(request.getTeacherId());
        AcademicYear academicYear =
                getAcademicYear(request.getAcademicYearId());
        Section section = getSection(request.getSectionId());
        Subject subject = getSubject(request.getSubjectId());

        validateTeacher(teacher);
        validateAcademicYear(academicYear);
        validateSection(section);
        validateSubject(subject);

        validateSectionBelongsToAcademicYear(
                section,
                academicYear
        );

        boolean duplicate =
                assignmentRepository
                        .existsByTeacherIdAndAcademicYearIdAndSectionIdAndSubjectIdAndIdNot(
                                teacher.getId(),
                                academicYear.getId(),
                                section.getId(),
                                subject.getId(),
                                id
                        );

        if (duplicate) {
            throw new DuplicateResourceException(
                    "Another teacher assignment already exists for this teacher, subject, section and academic year"
            );
        }

        assignment.setTeacher(teacher);
        assignment.setAcademicYear(academicYear);
        assignment.setSection(section);
        assignment.setSubject(subject);

        return map(
                assignmentRepository.save(assignment)
        );
    }

    public void delete(Long id) {

        TeacherAssignment assignment =
                assignmentRepository.findByIdForUpdate(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Teacher assignment not found: " + id
                                )
                        );

        assignmentRepository.delete(assignment);
    }

    private Teacher getTeacher(Long id) {

        return teacherRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher not found: " + id
                        )
                );
    }

    private AcademicYear getAcademicYear(Long id) {

        return academicYearRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Academic year not found: " + id
                        )
                );
    }

    private Section getSection(Long id) {

        return sectionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Section not found: " + id
                        )
                );
    }

    private Subject getSubject(Long id) {

        return subjectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subject not found: " + id
                        )
                );
    }

    private TeacherAssignment getAssignment(Long id) {

        return assignmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher assignment not found: " + id
                        )
                );
    }

    private void validateTeacher(Teacher teacher) {

        if (teacher.getStatus() != TeacherStatus.ACTIVE) {
            throw new BusinessRuleException(
                    "Only an ACTIVE teacher can receive an assignment"
            );
        }
    }

    private void validateAcademicYear(
            AcademicYear academicYear
    ) {

        if (academicYear.getStatus() == AcademicYearStatus.CLOSED) {
            throw new BusinessRuleException(
                    "A teacher cannot be assigned to a CLOSED academic year"
            );
        }
    }

    private void validateSection(Section section) {

        if (!section.isActive()) {
            throw new BusinessRuleException(
                    "Only an ACTIVE section can receive teacher assignments"
            );
        }
    }

    private void validateSubject(Subject subject) {

        if (!subject.isActive()) {
            throw new BusinessRuleException(
                    "Only an ACTIVE subject can be assigned to a teacher"
            );
        }
    }

    private void validateSectionBelongsToAcademicYear(
            Section section,
            AcademicYear academicYear
    ) {

        /*
         * Current Section entity does not contain an academicYearId.
         *
         * Therefore the section is linked to the academic year
         * through TeacherAssignment itself.
         *
         * Do not invent a Section -> AcademicYear relationship here.
         */
    }

    private TeacherAssignmentResponse map(
            TeacherAssignment assignment
    ) {

        Teacher teacher = assignment.getTeacher();
        AcademicYear year = assignment.getAcademicYear();
        Section section = assignment.getSection();
        Subject subject = assignment.getSubject();

        return TeacherAssignmentResponse.builder()
                .id(assignment.getId())

                .teacherId(teacher.getId())
                .employeeCode(teacher.getEmployeeCode())
                .teacherName(
                        teacher.getFirstName()
                                + " "
                                + teacher.getLastName()
                )

                .academicYearId(year.getId())
                .academicYearName(year.getName())

                .sectionId(section.getId())
                .sectionName(section.getName())

                .schoolClassId(
                        section.getSchoolClass().getId()
                )
                .schoolClassName(
                        section.getSchoolClass().getName()
                )

                .subjectId(subject.getId())
                .subjectCode(subject.getCode())
                .subjectName(subject.getName())

                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .version(assignment.getVersion())

                .build();
    }
}