package com.school.mamagement.entity;
import jakarta.persistence.*; import lombok.*;
@Entity
@Table(name="results", uniqueConstraints=@UniqueConstraint(name="uk_result_exam_student",columnNames={"exam_id","student_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result extends BaseEntity {
    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="exam_id",nullable=false, foreignKey=@ForeignKey(name="fk_result_exam")) private Exam exam;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="student_id",nullable=false,foreignKey=@ForeignKey(name="fk_result_student")) private Student student;
    @Column(name="total_marks",nullable=false,precision=10,scale=2) private java.math.BigDecimal totalMarks;
    @Column(name="obtained_marks",nullable=false,precision=10,scale=2) private java.math.BigDecimal obtainedMarks;
    @Column(nullable=false,precision=5,scale=2) private java.math.BigDecimal percentage; @Column(length=5) private String grade;
    @Column(nullable=false) private boolean passed;
}
