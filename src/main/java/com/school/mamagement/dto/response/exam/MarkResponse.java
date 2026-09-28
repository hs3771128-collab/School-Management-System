package com.school.mamagement.dto.response.exam; import lombok.*; import java.math.BigDecimal; import java.time.*;
@Getter @Builder public class MarkResponse { Long id,examSubjectId,studentId; String studentCode,subjectCode; BigDecimal marksObtained,maxMarks; LocalDateTime createdAt,updatedAt; }
