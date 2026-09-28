package com.school.mamagement.dto.response.fee; import com.school.mamagement.enums.PaymentMethod; import lombok.*; import java.math.BigDecimal; import java.time.LocalDate;
@Getter @Builder public class PaymentResponse { Long id,invoiceId; BigDecimal amount; LocalDate paymentDate; PaymentMethod method; String reference; }
