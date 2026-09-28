package com.school.mamagement.dto.response.academic; import lombok.*; import java.time.*;
@Getter @Builder public class SubjectResponse { Long id; String code,name,description; boolean active; LocalDateTime createdAt,updatedAt; }
