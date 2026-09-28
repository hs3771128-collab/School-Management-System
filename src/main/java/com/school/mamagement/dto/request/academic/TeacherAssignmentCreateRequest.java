package com.school.mamagement.dto.request.academic;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherAssignmentCreateRequest {

    @NotNull
    @Positive
    private Long teacherId;

    @NotNull
    @Positive
    private Long academicYearId;

    @NotNull
    @Positive
    private Long sectionId;

    @NotNull
    @Positive
    private Long subjectId;
}