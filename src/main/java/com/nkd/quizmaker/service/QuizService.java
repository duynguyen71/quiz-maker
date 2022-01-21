package com.nkd.quizmaker.service;

import com.nkd.quizmaker.mapper.QuizMapper;
import com.nkd.quizmaker.model.*;
import com.nkd.quizmaker.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final QuizRepository quizRepo;
    private final QuestionRepository questionRepo;
    private final OptionRepository optionRepo;
    private final SubjectRepository subjectRepo;
    private final UserRepository userRepository;
    private final QuizSubmissionRepository submissionRepo;

    /**
     * get quizzes
     *
     * @param
     * @return
     */
    public List<Quiz> getQuizzesNative(String subject, String title,Integer active,Integer status, Pageable pageable) {
        return quizRepo.getQuizzesNative(subject, title,active,status, pageable);
    }

    /**
     * find quiz by code
     *
     * @param code
     * @return
     */
    public Quiz getByCode(String code) {
        Optional<Quiz> optional = quizRepo.findByCodeAndActive(code, 1);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    /**
     * get quiz's questions
     */
    public List<Question> getQuizQuestions(String code) {
        Quiz quiz = getByCode(code);
        if (quiz != null) {
            return quiz.getQuestions();
        }
        return null;
    }

    /**
     * find by id
     *
     * @param id
     * @return
     */
    public Quiz getById(Long id) {
        Optional<Quiz> optional = quizRepo.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public Quiz save(Quiz quiz) {
        return quizRepo.save(quiz);
    }

    /**
     * get played time count
     *
     * @param quiz
     * @return
     */
    public int getPlayedCount(Quiz quiz) {
        return submissionRepo.countByQuiz(quiz);
    }

    /*
    find quiz detail by quizId
     */
    public ResponseEntity<?> findById(Long id) {
        Optional<Quiz> optional = quizRepo.findById(id);
        if (optional.isPresent())
            return ResponseEntity.ok(QuizMapper.toDto(optional.get()));
        return ResponseEntity.status(400).body("Can not find quiz with id: " + id);
    }

    /**
     * get questions
     *
     * @return
     */
    public List<Question> getQuestions() {
        return null;
    }


  public  Quiz getByIdAndUser(Long id,User user){
        Optional<Quiz> optional = quizRepo.findByIdAndOwners(id, user);
//        Optional<Quiz> optional = quizRepo.findByIdAndOwnersAndQuestions_Active(id, user,1);

        if(optional.isPresent())
            return optional.get();
        return null;
    }

    public Quiz getCurrentUserQuiz(Long quizId){
        Optional<Quiz> optional = quizRepo.findByIdAndOwners(quizId, getCurrentUser());
        if(optional.isPresent())
            return optional.get();
        return null;
    }

    public User getCurrentUser() {
        MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.getById(myUserDetails.getUser().getUid());
    }

    public double getTotalScore(Collection<Question> questions){
        double totalScore = 0.0;
        for (Question question:
             questions) {
            for (Option option :question.getOptions()) {
                totalScore+=option.getScore();
            }
        }
        return totalScore;

    }
}
