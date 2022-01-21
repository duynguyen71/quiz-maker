package com.nkd.quizmaker.service;

import com.nkd.quizmaker.model.Subject;
import com.nkd.quizmaker.repo.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepo;

    public List<Subject> getAll(){
        return subjectRepo.findAll();
    }

    public List<Subject> getSubjects(String title, Pageable pageable){
        return subjectRepo.getSubjects(title,pageable);
    }
    public Subject save(Subject subject){
        return subjectRepo.save(subject);
    }

    public Subject getById(long id){
        Optional<Subject> optional = subjectRepo.findById(id);
        if(optional.isPresent())
            return optional.get();
        return null;
    }

    public Subject getByName(String name){
        Optional<Subject> optional = subjectRepo.findByTitle(name);
        if(optional.isPresent())
            return optional.get();
        return null;
    }
}
