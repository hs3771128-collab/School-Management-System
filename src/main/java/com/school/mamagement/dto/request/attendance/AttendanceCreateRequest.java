package com.school.mamagement.dto.request.attendance;

import com.school.mamagement.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AttendanceCreateRequest {
    @NotNull
    @Positive
    private Long studentId;
    @NotNull
    private LocalDate attendanceDate;
    @NotNull
    private AttendanceStatus status;
    @Size(max = 300)
    private String remarks;
}
