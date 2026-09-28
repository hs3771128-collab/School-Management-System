package com.school.mamagement.entity;

import com.school.mamagement.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fee_invoices",
        uniqueConstraints = @UniqueConstraint(name = "uk_fee_invoice_number", columnNames = "invoice_number"),
        indexes = {
                @Index(name = "idx_fee_invoice_student", columnList = "student_id"),
                @Index(name = "idx_fee_invoice_year", columnList = "academic_year_id"),
                @Index(name = "idx_fee_invoice_status", columnList = "status")
        })
@Check(constraints = "total_amount > 0 AND paid_amount >= 0 AND paid_amount <= total_amount")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FeeInvoice extends BaseEntity {
    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_invoice_student"))
    private Student student;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false, foreignKey = @ForeignKey(name = "fk_invoice_year"))
    private AcademicYear academicYear;

    @Size(max = 50)
    @Column(name = "invoice_number", nullable = false, length = 50)
    private String invoiceNumber;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull @DecimalMin(value = "0.01")
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @NotNull @DecimalMin(value = "0.00")
    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.UNPAID;
}
