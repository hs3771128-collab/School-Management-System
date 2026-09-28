package com.school.mamagement.service;
import com.school.mamagement.entity.SystemCodeSequence;
import com.school.mamagement.repository.SystemCodeSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemCodeSequenceService {

    private final SystemCodeSequenceRepository sequenceRepository;

    @Transactional
    public long nextValue(String prefix) {

        if (prefix == null || prefix.isBlank()) {
            throw new IllegalArgumentException(
                    "Sequence prefix must not be blank"
            );
        }

        String normalizedPrefix = prefix.trim().toUpperCase();

        SystemCodeSequence sequence =
                sequenceRepository.findByPrefixForUpdate(normalizedPrefix)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No system code sequence configured for prefix: "
                                                + normalizedPrefix
                                )
                        );

        long nextValue = sequence.getNextValue() + 1;

        sequence.setNextValue(nextValue);

        return nextValue;
    }
}

