package com.school.mamagement.repository;

import com.school.mamagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByReference(String reference);

    List<Payment> findByInvoiceId(Long invoiceId);
}
