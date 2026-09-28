package com.school.mamagement.dto.response.fee;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class FeeStructureResponse {
    Long id, academicYearId, schoolClassId;
    String name;
    BigDecimal amount;
    boolean active;
}
