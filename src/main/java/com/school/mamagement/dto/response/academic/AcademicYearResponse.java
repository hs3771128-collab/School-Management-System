package com.school.mamagement.dto.response.academic;

import com.school.mamagement.enums.AcademicYearStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AcademicYearResponse {
    Long id;
    String name;
    LocalDate startDate, endDate;
    AcademicYearStatus status;
    LocalDateTime createdAt, updatedAt;
}
