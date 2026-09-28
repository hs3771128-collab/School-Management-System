package com.school.mamagement.dto.request.academic;
import jakarta.validation.constraints.*; import lombok.*;
@Getter @Setter public class SubjectCreateRequest { @NotBlank @Size(max=30) private String code; @NotBlank @Size(max=100) private String name; @Size(max=500) private String description; private Boolean active; }
