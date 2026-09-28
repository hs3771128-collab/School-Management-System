package com.school.mamagement.dto.request.academic;

import com.school.mamagement.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentCreateRequest {
    @NotNull
    @Positive
    private Long studentId;
    @NotNull
    @Positive
    private Long academicYearId;
    @NotNull
    @Positive
    private Long schoolClassId;
    @NotNull
    @Positive
    private Long sectionId;
    private EnrollmentStatus status;
}

