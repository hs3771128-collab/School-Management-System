package com.school.mamagement.dto.request.academic;
import jakarta.validation.constraints.*; import lombok.*;
@Getter @Setter public class SchoolClassCreateRequest { @NotBlank @Size(max=100) private String name; @NotNull @Min(1) private Integer gradeLevel; private Boolean active; }
