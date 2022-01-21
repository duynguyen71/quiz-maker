package com.nkd.quizmaker.helper;

import com.nkd.quizmaker.service.SubjectServiceHelper;
import com.nkd.quizmaker.model.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectHelp {

    private final SubjectServiceHelper helper;

    public ResponseEntity<?> save(Subject subject){
        return helper.save(subject);
    }

   public ResponseEntity<?> saveAll(Subject ...subject){
        return helper.saveAll(subject);
   }

    public ResponseEntity<?> findAll(){
        return helper.findAll() ;
    }
}
