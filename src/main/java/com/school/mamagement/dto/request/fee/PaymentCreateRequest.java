package com.school.mamagement.dto.request.fee;

import com.school.mamagement.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentCreateRequest {
    @NotNull
    @Positive
    private Long invoiceId;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
    @NotNull
    private LocalDate paymentDate;
    @NotNull
    private PaymentMethod method;
    @Size(max = 100)
    private String reference;
}
