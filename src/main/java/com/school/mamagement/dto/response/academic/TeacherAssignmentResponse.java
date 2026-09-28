package com.school.mamagement.dto.response.academic;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeacherAssignmentResponse {

    private Long id;

    private Long teacherId;
    private String employeeCode;
    private String teacherName;

    private Long academicYearId;
    private String academicYearName;

    private Long sectionId;
    private String sectionName;

    private Long schoolClassId;
    private String schoolClassName;

    private Long subjectId;
    private String subjectCode;
    private String subjectName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long version;
}