package com.school.mamagement.dto.request.exam;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MarkCreateRequest {
    @NotNull
    @Positive
    private Long examSubjectId;
    @NotNull
    @Positive
    private Long studentId;
    @NotNull
    @DecimalMin("0")
    private BigDecimal marksObtained;
}
