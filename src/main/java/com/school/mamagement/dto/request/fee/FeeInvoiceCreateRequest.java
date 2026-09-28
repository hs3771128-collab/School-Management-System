package com.school.mamagement.dto.request.fee;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FeeInvoiceCreateRequest {

    @NotNull
    @Positive
    private Long studentId;

    @NotNull
    @Positive
    private Long academicYearId;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal totalAmount;
}