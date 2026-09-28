package com.school.mamagement.dto.response.attendance; import com.school.mamagement.enums.AttendanceStatus; import lombok.*; import java.time.*;
@Getter @Builder public class AttendanceResponse { Long id,studentId; String studentCode; LocalDate attendanceDate; AttendanceStatus status; String remarks; LocalDateTime createdAt,updatedAt; }
