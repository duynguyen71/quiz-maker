package com.nkd.quizmaker.service;

import com.nkd.quizmaker.model.Question;
import com.nkd.quizmaker.model.Quiz;
import com.nkd.quizmaker.model.User;
import com.nkd.quizmaker.repo.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepo;


    public Question getById(long id){
        Optional<Question> optional = questionRepo.findById(id
        );
        if(optional.isPresent())
            return optional.get();
        return null;
    }
    public Question save(Question question){
        return questionRepo.save(question);
    }

    public List<Question> getQuestions(String title, Pageable pageable){
        return questionRepo.getQuestionsNative(title,pageable);
    }
    public Question getByUserAndId(User user,Long id){
    return questionRepo.findQuestionNative(user.getUid(), id).orElse(null);

    }



}
