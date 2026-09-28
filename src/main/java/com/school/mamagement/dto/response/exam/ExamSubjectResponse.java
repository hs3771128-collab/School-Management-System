package com.school.mamagement.dto.response.exam; import lombok.*; import java.math.BigDecimal;
@Getter @Builder public class ExamSubjectResponse { Long id,examId,subjectId; String subjectCode,subjectName; BigDecimal maxMarks,passingMarks; }
