package com.school.mamagement.controller;

import com.school.mamagement.service.FeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fees")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class FeeController {
    private final FeeService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/structures")
    public com.school.mamagement.dto.response.fee.FeeStructureResponse structure
            (@Valid @RequestBody com.school.mamagement.dto.request.fee.FeeStructureCreateRequest r) {
        return service.createStructure(r);
    }

    @GetMapping("/structures/academic-year/{yearId}")
    public List<com.school.mamagement.dto.response.fee.FeeStructureResponse> structures(@PathVariable Long yearId) {
        return service.structures(yearId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/invoices")
    public com.school.mamagement.dto.response.fee.FeeInvoiceResponse invoice(@Valid @RequestBody com.school.mamagement.dto.request.fee.FeeInvoiceCreateRequest r) {
        return service.createInvoice(r);
    }

    @GetMapping("/invoices/student/{studentId}")
    public List<com.school.mamagement.dto.response.fee.FeeInvoiceResponse> invoices(@PathVariable Long studentId) {
        return service.invoicesByStudent(studentId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/payments")
    public com.school.mamagement.dto.response.fee.PaymentResponse payment(@Valid @RequestBody com.school.mamagement.dto.request.fee.PaymentCreateRequest r) {
        return service.recordPayment(r);
    }
}
