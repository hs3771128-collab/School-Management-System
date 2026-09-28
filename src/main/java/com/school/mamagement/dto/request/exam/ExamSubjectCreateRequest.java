package com.school.mamagement.dto.request.exam;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExamSubjectCreateRequest {
    @NotNull
    @Positive
    private Long examId;
    @NotNull
    @Positive
    private Long subjectId;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal maxMarks;
    @NotNull
    @DecimalMin("0")
    private BigDecimal passingMarks;
}
