package com.school.mamagement.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SystemCodeGenerator {

    private final SystemCodeSequenceService sequenceService;

    public SystemCodeGenerator(SystemCodeSequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }

    public String adminCode() {
        return generate("ADM");
    }

    public String superAdminCode() {
        return generate("SADM");
    }

    public String teacherCode() {
        return generate("TCH");
    }

    public String studentCode() {
        return generate("STU");
    }
    public String invoiceNumber() {
        return generate("INV");
    }

    private String generate(String prefix) {
        long sequence = sequenceService.nextValue(prefix);
        return String.format("%s-%06d", prefix, sequence);
    }
}