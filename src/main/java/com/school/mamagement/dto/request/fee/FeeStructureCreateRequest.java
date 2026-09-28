package com.school.mamagement.dto.request.fee;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FeeStructureCreateRequest {
    @NotNull
    @Positive
    private Long academicYearId;
    @NotNull
    @Positive
    private Long schoolClassId;
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal amount;
    private Boolean active;
}
