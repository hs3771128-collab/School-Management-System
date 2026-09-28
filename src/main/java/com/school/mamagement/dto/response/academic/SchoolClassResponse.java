package com.school.mamagement.dto.response.academic; import lombok.*; import java.time.*;
@Getter @Builder public class SchoolClassResponse { Long id; String name; Integer gradeLevel; boolean active; LocalDateTime createdAt,updatedAt; }
