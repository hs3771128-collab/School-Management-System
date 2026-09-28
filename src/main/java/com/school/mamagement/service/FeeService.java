package com.school.mamagement.service;

import com.school.mamagement.dto.request.fee.FeeInvoiceCreateRequest;
import com.school.mamagement.dto.request.fee.FeeStructureCreateRequest;
import com.school.mamagement.dto.request.fee.PaymentCreateRequest;
import com.school.mamagement.dto.response.fee.FeeInvoiceResponse;
import com.school.mamagement.dto.response.fee.FeeStructureResponse;
import com.school.mamagement.dto.response.fee.PaymentResponse;
import com.school.mamagement.entity.*;
import com.school.mamagement.enums.PaymentStatus;
import com.school.mamagement.exception.BusinessRuleException;
import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.exception.ResourceNotFoundException;
import com.school.mamagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class FeeService {
    private final FeeStructureRepository structures;
    private final FeeInvoiceRepository invoices;
    private final PaymentRepository payments;
    private final AcademicYearRepository years;
    private final SchoolClassRepository classes;
    private final StudentRepository students;
    private final EnrollmentRepository enrollments;
    private final SystemCodeGenerator systemCodeGenerator;

    public FeeStructureResponse createStructure(FeeStructureCreateRequest r) {
        var y = year(r.getAcademicYearId());
        var c = clazz(r.getSchoolClassId());
        String n = clean(r.getName());
        if (r.getAmount().signum() < 0) throw new BusinessRuleException("Fee amount cannot be negative");
        if (structures.existsByAcademicYearIdAndSchoolClassIdAndNameIgnoreCase(y.getId(), c.getId(), n))
            throw new DuplicateResourceException("Fee structure already exists: " + n);
        FeeStructure e = new FeeStructure();
        e.setAcademicYear(y);
        e.setSchoolClass(c);
        e.setName(n);
        e.setAmount(r.getAmount().setScale(2));
        e.setActive(r.getActive() == null || r.getActive());
        return map(structures.save(e));
    }

    @Transactional(readOnly = true)
    public List<FeeStructureResponse> structures(Long yearId) {
        year(yearId);
        return structures.findByAcademicYearId(yearId).stream().map(this::map).toList();
    }

    public FeeInvoiceResponse createInvoice(FeeInvoiceCreateRequest r) {

        var s = student(r.getStudentId());
        var y = year(r.getAcademicYearId());

        if (!enrollments.existsByStudentIdAndAcademicYearId(s.getId(), y.getId())) {
            throw new BusinessRuleException(
                    "Student is not enrolled in the invoice academic year"
            );
        }

        if (r.getDueDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(
                    "Invoice due date cannot be in the past"
            );
        }

        FeeInvoice e = new FeeInvoice();

        e.setStudent(s);
        e.setAcademicYear(y);

        // System automatically generates the invoice number
        e.setInvoiceNumber(systemCodeGenerator.invoiceNumber());

        e.setDueDate(r.getDueDate());
        e.setTotalAmount(r.getTotalAmount().setScale(2));
        e.setPaidAmount(BigDecimal.ZERO.setScale(2));
        e.setStatus(PaymentStatus.UNPAID);

        return map(invoices.save(e));
    }

    @Transactional(readOnly = true)
    public List<FeeInvoiceResponse> invoicesByStudent(Long studentId) {
        student(studentId);
        return invoices.findByStudentId(studentId).stream().map(this::map).toList();
    }

    public PaymentResponse recordPayment(PaymentCreateRequest r) {
        FeeInvoice i = invoices.findByIdForUpdate(r.getInvoiceId()).orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + r.getInvoiceId()));
        if (r.getPaymentDate().isAfter(LocalDate.now()))
            throw new BusinessRuleException("Payment date cannot be in the future");
        if (r.getAmount().signum() <= 0) throw new BusinessRuleException("Payment amount must be positive");
        BigDecimal balance = i.getTotalAmount().subtract(i.getPaidAmount());
        if (r.getAmount().compareTo(balance) > 0) throw new BusinessRuleException("Payment exceeds invoice balance");
        String ref = clean(r.getReference());
        if (ref != null && payments.existsByReference(ref))
            throw new DuplicateResourceException("Payment reference already exists");
        Payment p = new Payment();
        p.setInvoice(i);
        p.setAmount(r.getAmount().setScale(2));
        p.setPaymentDate(r.getPaymentDate());
        p.setMethod(r.getMethod());
        p.setReference(ref);
        i.setPaidAmount(i.getPaidAmount().add(p.getAmount()).setScale(2));
        i.setStatus(i.getPaidAmount().compareTo(i.getTotalAmount()) == 0 ? PaymentStatus.PAID : PaymentStatus.PARTIALLY_PAID);
        payments.save(p);
        invoices.save(i);
        return map(p);
    }

    @Transactional(readOnly = true)
    public FeeInvoiceResponse invoice(Long id) {
        return map(invoices.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id)));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> payments(Long invoiceId) {
        invoice(invoiceId);
        return payments.findByInvoiceId(invoiceId).stream().map(this::map).toList();
    }

    private AcademicYear year(Long id) {
        return years.findById(id).orElseThrow(() -> new ResourceNotFoundException("Academic year not found: " + id));
    }

    private SchoolClass clazz(Long id) {
        return classes.findById(id).orElseThrow(() -> new ResourceNotFoundException("Class not found: " + id));
    }

    private Student student(Long id) {
        return students.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }

    private String clean(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private FeeStructureResponse map(FeeStructure e) {
        return FeeStructureResponse.builder().id(e.getId()).academicYearId(e.getAcademicYear().getId()).schoolClassId(e.getSchoolClass().getId()).name(e.getName()).amount(e.getAmount()).active(e.isActive()).build();
    }

    private FeeInvoiceResponse map(FeeInvoice e) {
        refreshOverdueStatus(e);
        return FeeInvoiceResponse.builder().id(e.getId()).studentId(e.getStudent().getId()).academicYearId(e.getAcademicYear().getId()).invoiceNumber(e.getInvoiceNumber()).studentCode(e.getStudent().getStudentCode()).dueDate(e.getDueDate()).totalAmount(e.getTotalAmount()).paidAmount(e.getPaidAmount()).balance(e.getTotalAmount().subtract(e.getPaidAmount())).status(e.getStatus()).build();
    }

    private void refreshOverdueStatus(FeeInvoice e) {
        if (e.getStatus() != PaymentStatus.PAID && e.getStatus() != PaymentStatus.CANCELLED && e.getDueDate().isBefore(LocalDate.now()))
            e.setStatus(PaymentStatus.OVERDUE);
    }

    private PaymentResponse map(Payment e) {
        return PaymentResponse.builder().id(e.getId()).invoiceId(e.getInvoice().getId()).amount(e.getAmount()).paymentDate(e.getPaymentDate()).method(e.getMethod()).reference(e.getReference()).build();
    }
}
