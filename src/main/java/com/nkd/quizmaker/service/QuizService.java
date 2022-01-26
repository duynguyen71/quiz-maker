package com.nkd.quizmaker.service;

import com.nkd.quizmaker.mapper.QuestionMapper;
import com.nkd.quizmaker.mapper.QuizMapper;
import com.nkd.quizmaker.model.*;
import com.nkd.quizmaker.repo.*;
import com.nkd.quizmaker.response.BaseResponse;
import com.nkd.quizmaker.response.QuizOverview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<Quiz> getQuizzesNative(String subject, String title, Integer active, Integer status, Pageable pageable) {
        return quizRepo.getQuizzesNative(subject, title, active, status, pageable);
    }

    /**
     * find quiz by code
     *
     * @param code
     * @return
     */
    public Quiz getByCodeAndActiveAndStatus(String code, int active, int status) {
        Optional<Quiz> optional = quizRepo.findByCodeAndActiveAndStatus(code, active, status);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public Quiz getByCode(String code) {
        return quizRepo.findByCode(code).orElse(null);
    }

    public Quiz getByCodeAndActiveAndStatus(String code, Integer active, Integer status) {
        return quizRepo.findByCodeAndActiveAndStatus(code, active, status).orElse(null);
    }

    /**
     * get quiz's questions
     */
    public List<Question> getQuizQuestions(String code) {
        Quiz quiz = getByCodeAndActiveAndStatus(code, 1, 2);
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

    public Quiz getQuizByCodeAndStatusNative(String code, Integer active, Integer status) {
        return quizRepo.findQuizByCodeNative(code, status, active).orElse(null);
    }

    public QuizOverview getAssignedQuizDetail(String code) {
        Optional<Quiz> optional = quizRepo.findQuizByCodeNative(code, 1, 1);

        if (optional.isPresent()) {
            Quiz quiz = optional.get();
            QuizOverview quizOverview = QuizMapper.toQuizOverview(quiz, getQuizScore(quiz));
            quizOverview.setQuestions(questionRepo.findAllByQuizzesAndActive(quiz, 1)
                    .stream()
                    .map(QuestionMapper::toQuestionResponse)
                    .collect(Collectors.toList()));
            return quizOverview;
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


    public List<Question> getQuizQuestions(Quiz quiz, Integer active) {
        return questionRepo.findAllByQuizzesAndActive(quiz, active);
    }

    public Quiz getByIdAndActiveAndStatus(Long id, int active, int status) {
        return quizRepo.findByIdAndActiveAndStatus(id, active, status).orElse(null);
    }

    public double getQuizScore(Quiz quiz) {
        double quizScore = 0;
        List<Question> quizQuestions = getQuizQuestions(quiz, 1);
        for (Question question :
                quizQuestions) {
            for (Option option :
                    question.getOptions()) {
                quizScore += option.getScore();
            }
        }

        return quizScore;
    }


    public Quiz getByIdAndUser(Long id, User user) {
        return quizRepo.findByIdAndOwners(id, user).orElse(null);
    }

    public Quiz getCurrentUserQuiz(Long quizId) {
        return quizRepo.findByIdAndOwners(quizId, getCurrentUser()).orElse(null);

    }

    public User getCurrentUser() {
        MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.getById(myUserDetails.getUser().getUid());
    }


    public List<Quiz> getCurrentUserQuizzes(Integer status, Integer active, Pageable pageable) {
        List<Quiz> quizzes = quizRepo.findByUser(getCurrentUser().getUid(), status, active, pageable);
        return quizzes;
    }
}
