package com.nkd.quizmaker.controller;

import com.nkd.quizmaker.helper.FileHelper;
import com.nkd.quizmaker.helper.QuizHelper;
import com.nkd.quizmaker.helper.SubjectHelper;
import com.nkd.quizmaker.helper.UserHelper;
import com.nkd.quizmaker.request.SubjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PulController {

    private final QuizHelper quizHelper;
    private final SubjectHelper subjectHelper;
    private final UserHelper userHelper;
    private final FileHelper fileHelper;

    /**
     * get quizzes
     */
    @GetMapping("/quizzes")
    public ResponseEntity<?> getQuizzes(@RequestParam Map<String, String> params) {
        return quizHelper.getQuizzes(params);
    }

    /**
     * find quiz by code
     */
    @GetMapping("/quizzes/{code}")
    public ResponseEntity<?> findQuizByCode(@PathVariable String code, @RequestParam Map<String, String> params) {
        return quizHelper.findQuizByCode(code, params);
    }

    /**
     * find quiz questions
     */
    @GetMapping("/quizzes/{code}/questions")
    public ResponseEntity<?> getQuizQuestions(@PathVariable String code) {
        return quizHelper.getQuizQuestions(code);
    }

    /**
     * get all subjects
     */
    @GetMapping("/subjects")
    public ResponseEntity<?> getSubjects(@RequestParam Map<String, String> params) {
        return subjectHelper.getSubjects(params);
    }


    @PostMapping("/subjects")
    public ResponseEntity<?> saveSubject(@RequestBody @Valid SubjectRequest subjectRequest) {
        return subjectHelper.saveSubject(subjectRequest);
    }


    /**
     * validation input field
     *
     * @param input
     * @param value
     * @return
     */
    @GetMapping("/validation-input")
    public ResponseEntity<?> validationInput(@RequestParam("input") String input, @RequestParam("value") String value) {
        return userHelper.validationInput(input, value);
    }


    @GetMapping("/files/{name}")
    public ResponseEntity<?> getImage(@PathVariable String name) {
        return fileHelper.getImage(name);
    }

}
