package com.school.mamagement.dto.request.academic;
import jakarta.validation.constraints.*; import lombok.*;
@Getter @Setter public class SectionCreateRequest { @NotNull @Positive private Long schoolClassId; @NotBlank @Size(max=50) private String name; @NotNull @Min(1) private Integer capacity; private Boolean active; }
