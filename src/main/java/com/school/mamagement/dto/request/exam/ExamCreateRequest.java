package com.school.mamagement.dto.request.exam;

import com.school.mamagement.enums.ExamStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExamCreateRequest {
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotNull
    @Positive
    private Long academicYearId;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private ExamStatus status;
}
