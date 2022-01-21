package com.nkd.quizmaker.service;

import com.nkd.quizmaker.model.Option;
import com.nkd.quizmaker.model.Question;
import com.nkd.quizmaker.repo.OptionRepository;
import com.nkd.quizmaker.repo.QuestionRepository;
import com.nkd.quizmaker.request.OptionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component(value = "OptionHelper")
@RequiredArgsConstructor
@Slf4j
public class OptionServiceHelper {

    private final OptionRepository optionRepo;
    private final QuestionRepository questionRepo;


    public ResponseEntity<?> saveToQuestion(Long questionId, OptionRequest optionRequest) {
        //update if option exists by id
        if (questionId == null && !questionRepo.existsById(questionId)) {
            return ResponseEntity.badRequest().body("Question parent is not exists");
        }
        Question savedQuestion = questionRepo.getById(questionId);
        //update
        if (optionRequest.getId() != null
                && optionRepo.existsById(optionRequest.getId())) {
            Option savedOption = optionRepo.getById(optionRequest.getId());
            savedOption.setScore(optionRequest.getScore());
            savedOption.setFExplanation(optionRequest.getFExplanation());
            savedOption.setTExplanation(optionRequest.getTExplanation());
            savedOption.setContent(optionRequest.getContent());
            optionRepo.save(savedOption);
            return ResponseEntity.ok("Update option success");
        } else {
            Option option = new Option();
            option.setScore(optionRequest.getScore());
            option.setFExplanation(optionRequest.getFExplanation());
            option.setTExplanation(optionRequest.getTExplanation());
            option.setContent(optionRequest.getContent());
            option.getQuestions().add(savedQuestion);
            optionRepo.save(option);
            return ResponseEntity.ok("Add new option success");

        }
    }

    //savedQuestion must present
    void saveOption(Question savedQuestion, OptionRequest optionRequest) {
        //update option
        if (optionRequest.getId() != null
                && optionRepo.existsById(optionRequest.getId())) {
            Option option = optionRepo.getById(optionRequest.getId());
            option.setContent(optionRequest.getContent());
            option.setScore(optionRequest.getScore());
            option.setTExplanation(optionRequest.getTExplanation());
            option.setFExplanation(optionRequest.getFExplanation());
            //if option not contain saved question -> add to saved question
            if (!option.getQuestions().contains(savedQuestion)) {
                option.getQuestions().add(savedQuestion);
            }
            optionRepo.save(option);
        }
        //add brand new option and add to question
        else {
            Option option = new Option();
            option.setContent(optionRequest.getContent());
            option.setScore(optionRequest.getScore());
            option.setTExplanation(optionRequest.getTExplanation());
            option.setFExplanation(optionRequest.getFExplanation());
            option.getQuestions().add(savedQuestion);
            optionRepo.save(option);
        }
    }

    public double getTotalScore(Long... ids) {
        double total = Arrays.stream(ids)
                .map(optionRepo::findById)
                .filter(Optional::isPresent)
                .mapToDouble(optional -> optional.get().getScore()).sum();
        return total;
    }


}
