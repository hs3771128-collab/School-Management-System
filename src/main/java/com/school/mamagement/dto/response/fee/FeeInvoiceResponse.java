package com.school.mamagement.dto.response.fee;

import com.school.mamagement.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class FeeInvoiceResponse {
    Long id, studentId, academicYearId;
    String invoiceNumber, studentCode;
    LocalDate dueDate;
    BigDecimal totalAmount, paidAmount, balance;
    PaymentStatus status;
}
