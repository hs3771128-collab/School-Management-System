package com.school.mamagement.dto.request.academic;

import com.school.mamagement.enums.AcademicYearStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AcademicYearCreateRequest {
    @NotBlank
    @Size(max = 50)
    private String name;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private AcademicYearStatus status;
}
