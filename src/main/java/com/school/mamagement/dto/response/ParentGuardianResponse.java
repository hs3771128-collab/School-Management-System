package com.school.mamagement.dto.response;

import com.school.mamagement.enums.RelationshipType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ParentGuardianResponse {

    private Long id;
    private Long studentId;
    private String studentCode;
    private String studentName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String cnic;
    private String occupation;
    private RelationshipType relationshipType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
