package com.school.mamagement.dto.response.academic; import lombok.*; import java.time.*;
@Getter @Builder public class SectionResponse { Long id; String name; Long schoolClassId; String schoolClassName; Integer capacity; boolean active; LocalDateTime createdAt,updatedAt; }
