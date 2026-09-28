package com.school.mamagement.service;

import com.school.mamagement.dto.request.exam.ExamCreateRequest;
import com.school.mamagement.dto.request.exam.ExamSubjectCreateRequest;
import com.school.mamagement.dto.request.exam.MarkCreateRequest;
import com.school.mamagement.dto.response.exam.ExamResponse;
import com.school.mamagement.dto.response.exam.ExamSubjectResponse;
import com.school.mamagement.dto.response.exam.MarkResponse;
import com.school.mamagement.dto.response.exam.ResultResponse;
import com.school.mamagement.entity.AcademicYear;
import com.school.mamagement.entity.Exam;
import com.school.mamagement.entity.ExamSubject;
import com.school.mamagement.entity.Mark;
import com.school.mamagement.entity.Result;
import com.school.mamagement.entity.Student;
import com.school.mamagement.entity.Subject;
import com.school.mamagement.enums.ExamStatus;
import com.school.mamagement.exception.BusinessRuleException;
import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.exception.ResourceNotFoundException;
import com.school.mamagement.repository.AcademicYearRepository;
import com.school.mamagement.repository.EnrollmentRepository;
import com.school.mamagement.repository.ExamRepository;
import com.school.mamagement.repository.ExamSubjectRepository;
import com.school.mamagement.repository.MarkRepository;
import com.school.mamagement.repository.ResultRepository;
import com.school.mamagement.repository.StudentRepository;
import com.school.mamagement.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

    private final ExamRepository exams;
    private final AcademicYearRepository academicYears;
    private final SubjectRepository subjects;
    private final ExamSubjectRepository examSubjects;
    private final StudentRepository students;
    private final MarkRepository marks;
    private final ResultRepository results;
    private final EnrollmentRepository enrollments;

    public ExamResponse createExam(ExamCreateRequest request) {
        AcademicYear academicYear = getAcademicYear(request.getAcademicYearId());

        validateExamDates(request.getStartDate(), request.getEndDate());
        validateDatesInsideAcademicYear(
                academicYear,
                request.getStartDate(),
                request.getEndDate()
        );

        String name = clean(request.getName());
        if (exams.existsByAcademicYearIdAndNameIgnoreCase(academicYear.getId(), name)) {
            throw new DuplicateResourceException("Exam already exists: " + name);
        }

        Exam exam = new Exam();
        exam.setName(name);
        exam.setAcademicYear(academicYear);
        exam.setStartDate(request.getStartDate());
        exam.setEndDate(request.getEndDate());
        exam.setStatus(request.getStatus() == null ? ExamStatus.PLANNED : request.getStatus());

        return toExamResponse(exams.save(exam));
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> exams(Long academicYearId) {
        getAcademicYear(academicYearId);

        return exams.findByAcademicYearId(academicYearId)
                .stream()
                .map(this::toExamResponse)
                .toList();
    }

    public ExamSubjectResponse addSubject(ExamSubjectCreateRequest request) {
        Exam exam = getExam(request.getExamId());
        Subject subject = getSubject(request.getSubjectId());

        validateExamSubjectMarks(request.getMaxMarks(), request.getPassingMarks());

        if (examSubjects.existsByExamIdAndSubjectId(exam.getId(), subject.getId())) {
            throw new DuplicateResourceException("Subject is already part of this exam");
        }

        ExamSubject examSubject = new ExamSubject();
        examSubject.setExam(exam);
        examSubject.setSubject(subject);
        examSubject.setMaxMarks(request.getMaxMarks());
        examSubject.setPassingMarks(request.getPassingMarks());

        return toExamSubjectResponse(examSubjects.save(examSubject));
    }

    @Transactional(readOnly = true)
    public List<ExamSubjectResponse> subjects(Long examId) {
        getExam(examId);

        return examSubjects.findByExamId(examId)
                .stream()
                .map(this::toExamSubjectResponse)
                .toList();
    }

    public MarkResponse recordMark(MarkCreateRequest request) {
        ExamSubject examSubject = examSubjects.findById(request.getExamSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exam subject not found: " + request.getExamSubjectId()
                ));

        Student student = getStudent(request.getStudentId());

        if (request.getMarksObtained().signum() < 0) {
            throw new BusinessRuleException("Marks obtained cannot be negative");
        }

        if (request.getMarksObtained().compareTo(examSubject.getMaxMarks()) > 0) {
            throw new BusinessRuleException("Marks obtained cannot exceed maximum marks");
        }

        Long academicYearId = examSubject.getExam().getAcademicYear().getId();
        if (!enrollments.existsByStudentIdAndAcademicYearId(student.getId(), academicYearId)) {
            throw new BusinessRuleException("Student is not enrolled in the exam academic year");
        }

        Mark mark = marks.findByExamSubjectIdAndStudentId(examSubject.getId(), student.getId())
                .orElseGet(Mark::new);

        mark.setExamSubject(examSubject);
        mark.setStudent(student);
        mark.setMarksObtained(request.getMarksObtained());

        return toMarkResponse(marks.save(mark));
    }

    public ResultResponse generateResult(Long examId, Long studentId) {
        Exam exam = getExam(examId);
        Student student = getStudent(studentId);

        List<ExamSubject> examSubjectsForExam = examSubjects.findByExamId(examId);
        if (examSubjectsForExam.isEmpty()) {
            throw new BusinessRuleException("No subjects have been configured for this exam");
        }

        Long academicYearId = exam.getAcademicYear().getId();
        if (!enrollments.existsByStudentIdAndAcademicYearId(studentId, academicYearId)) {
            throw new BusinessRuleException("Student is not enrolled in the exam academic year");
        }

        BigDecimal totalMarks = BigDecimal.ZERO;
        BigDecimal obtainedMarks = BigDecimal.ZERO;
        boolean passedAllSubjects = true;

        for (ExamSubject examSubject : examSubjectsForExam) {
            totalMarks = totalMarks.add(examSubject.getMaxMarks());

            Mark mark = marks.findByExamSubjectIdAndStudentId(examSubject.getId(), studentId)
                    .orElseThrow(() -> new BusinessRuleException(
                            "Marks are incomplete for subject: "
                                    + examSubject.getSubject().getName()
                    ));

            obtainedMarks = obtainedMarks.add(mark.getMarksObtained());

            if (mark.getMarksObtained().compareTo(examSubject.getPassingMarks()) < 0) {
                passedAllSubjects = false;
            }
        }

        BigDecimal percentage = obtainedMarks
                .multiply(BigDecimal.valueOf(100))
                .divide(totalMarks, 2, RoundingMode.HALF_UP);

        String grade = calculateGrade(percentage);
        boolean passed = passedAllSubjects && !"F".equals(grade);

        Result result = results.findByExamIdAndStudentId(examId, studentId)
                .orElseGet(Result::new);

        result.setExam(exam);
        result.setStudent(student);
        result.setTotalMarks(totalMarks);
        result.setObtainedMarks(obtainedMarks);
        result.setPercentage(percentage);
        result.setGrade(grade);
        result.setPassed(passed);

        return toResultResponse(results.save(result));
    }

    @Transactional(readOnly = true)
    public ResultResponse getResult(Long examId, Long studentId) {
        getExam(examId);
        getStudent(studentId);

        return results.findByExamIdAndStudentId(examId, studentId)
                .map(this::toResultResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Result not found for exam and student"
                ));
    }

    private Exam getExam(Long id) {
        return exams.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + id));
    }

    private AcademicYear getAcademicYear(Long id) {
        return academicYears.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found: " + id));
    }

    private Subject getSubject(Long id) {
        return subjects.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + id));
    }

    private Student getStudent(Long id) {
        return students.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }

    private void validateExamDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            throw new BusinessRuleException("Exam end date must be after start date");
        }
    }

    private void validateDatesInsideAcademicYear(
            AcademicYear academicYear,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (startDate.isBefore(academicYear.getStartDate())
                || endDate.isAfter(academicYear.getEndDate())) {
            throw new BusinessRuleException("Exam dates must fall within the academic year");
        }
    }

    private void validateExamSubjectMarks(BigDecimal maxMarks, BigDecimal passingMarks) {
        if (maxMarks == null || maxMarks.signum() <= 0) {
            throw new BusinessRuleException("Maximum marks must be greater than zero");
        }

        if (passingMarks == null || passingMarks.signum() < 0) {
            throw new BusinessRuleException("Passing marks cannot be negative");
        }

        if (passingMarks.compareTo(maxMarks) > 0) {
            throw new BusinessRuleException("Passing marks cannot exceed maximum marks");
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String calculateGrade(BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) return "A";
        if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) return "B";
        if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) return "C";
        if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) return "D";
        return "F";
    }

    private ExamResponse toExamResponse(Exam exam) {
        return ExamResponse.builder()
                .id(exam.getId())
                .academicYearId(exam.getAcademicYear().getId())
                .academicYearName(exam.getAcademicYear().getName())
                .name(exam.getName())
                .startDate(exam.getStartDate())
                .endDate(exam.getEndDate())
                .status(exam.getStatus())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .build();
    }

    private ExamSubjectResponse toExamSubjectResponse(ExamSubject examSubject) {
        return ExamSubjectResponse.builder()
                .id(examSubject.getId())
                .examId(examSubject.getExam().getId())
                .subjectId(examSubject.getSubject().getId())
                .subjectCode(examSubject.getSubject().getCode())
                .subjectName(examSubject.getSubject().getName())
                .maxMarks(examSubject.getMaxMarks())
                .passingMarks(examSubject.getPassingMarks())
                .build();
    }

    private MarkResponse toMarkResponse(Mark mark) {
        return MarkResponse.builder()
                .id(mark.getId())
                .examSubjectId(mark.getExamSubject().getId())
                .studentId(mark.getStudent().getId())
                .studentCode(mark.getStudent().getStudentCode())
                .subjectCode(mark.getExamSubject().getSubject().getCode())
                .marksObtained(mark.getMarksObtained())
                .maxMarks(mark.getExamSubject().getMaxMarks())
                .createdAt(mark.getCreatedAt())
                .updatedAt(mark.getUpdatedAt())
                .build();
    }

    private ResultResponse toResultResponse(Result result) {
        return ResultResponse.builder()
                .id(result.getId())
                .examId(result.getExam().getId())
                .studentId(result.getStudent().getId())
                .examName(result.getExam().getName())
                .studentCode(result.getStudent().getStudentCode())
                .totalMarks(result.getTotalMarks())
                .obtainedMarks(result.getObtainedMarks())
                .percentage(result.getPercentage())
                .grade(result.getGrade())
                .passed(result.isPassed())
                .build();
    }
}
