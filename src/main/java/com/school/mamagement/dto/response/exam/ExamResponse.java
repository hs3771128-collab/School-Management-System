package com.school.mamagement.dto.response.exam; import com.school.mamagement.enums.ExamStatus; import lombok.*; import java.time.*;
@Getter @Builder public class ExamResponse { Long id,academicYearId; String name,academicYearName; LocalDate startDate,endDate; ExamStatus status; LocalDateTime createdAt,updatedAt; }
