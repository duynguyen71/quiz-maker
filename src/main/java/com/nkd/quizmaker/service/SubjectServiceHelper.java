package com.nkd.quizmaker.service;

import com.nkd.quizmaker.dto.SubjectDto;
import com.nkd.quizmaker.mapper.SubjectMapper;
import com.nkd.quizmaker.model.Subject;
import com.nkd.quizmaker.repo.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceHelper {

    private final SubjectRepository subjectRepo;

    public ResponseEntity<?> save(Subject subject) {
        if (!subjectRepo.existsByTitle(subject.getTitle()))
            return ResponseEntity.ok(subjectRepo.save(subject).getId());
        return ResponseEntity.badRequest().body("Subject :" + subject.getTitle() + " existed!");
    }


    public ResponseEntity<?> saveAll(Subject... subjects) {
        List<Subject> savedSubject = Arrays.stream(subjects)
                .map(subject -> !subjectRepo.existsByTitle(subject.getTitle()) ?
                        subjectRepo.save(subject) : null)
                .collect(Collectors.toList());
        return ResponseEntity.ok(savedSubject);
    }

    public ResponseEntity<?> findAll() {
        List<SubjectDto> rs = subjectRepo.findAll().stream().map(SubjectMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(rs);
    }


}
