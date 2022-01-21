package com.nkd.quizmaker.service;

import com.nkd.quizmaker.dto.OptionDto;
import com.nkd.quizmaker.dto.QuestionDto;
import com.nkd.quizmaker.mapper.QuestionMapper;
import com.nkd.quizmaker.model.*;
import com.nkd.quizmaker.repo.OptionRepository;
import com.nkd.quizmaker.repo.QuestionRepository;
import com.nkd.quizmaker.repo.QuizRepository;
import com.nkd.quizmaker.repo.UserRepository;
import com.nkd.quizmaker.request.QuizSubmissionRequest;
import com.nkd.quizmaker.request.SubmissionAnswerRequest;
import com.nkd.quizmaker.repo.QuizSubmissionRepository;
import com.nkd.quizmaker.response.QuizSubmitResultResponse;
import com.nkd.quizmaker.repo.SubmissionAnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("MemberQuizSubmissionHelper")
@RequiredArgsConstructor
@Slf4j
public class MemberQuizSubmissionHelper {

    private final QuizSubmissionRepository quizSubmissionRepo;
    private final SubmissionAnswerRepository submissionAnswerRepo;
    private final QuizRepository quizRepo;
    private final QuestionRepository questionRepo;
    private final OptionRepository optionRepo;
    private final UserRepository userRepo;

    /**
     * Calculate score and save submit answers
     *
     * @param submissionRequest
     * @return total score a
     */
    public ResponseEntity<?> submitAnswers(QuizSubmissionRequest submissionRequest) {
        User user = userRepo.getById(getCurrentUid());
        Long quizId = submissionRequest.getQuizId();
        Optional<Quiz> optionalQuiz = quizRepo.findById(quizId);
        //check quiz id not null
        if (optionalQuiz.isEmpty())
            return ResponseEntity.badRequest().body("Quiz is not exists");
//        check use submitted or not
        if (quizSubmissionRepo.existsByQuizAndUser(optionalQuiz.get(), user))
            return ResponseEntity.badRequest().body("You already have been submitted answers");
        //create submission
        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setQuiz(optionalQuiz.get());
        quizSubmission.setStatus(1);
        quizSubmission.setUser(user);
        quizSubmission = quizSubmissionRepo.save(quizSubmission);
        //
        double totalScore = 0.0;
        for (SubmissionAnswerRequest answerRequest
                : submissionRequest.getAnswers()) {
            //create submission answer
            Optional<Question> optionalQuestion =
                    questionRepo.findById(answerRequest.getQuestionId());
            //get question
            if (optionalQuestion.isPresent()) {
                Question question = optionalQuestion.get();

                //loop all answer options in question requests
                double tmpScore = 0.0;
                for (Long optionIdRequest :
                        answerRequest.getOptions()) {
                    Option option =
                            getOptionDto(question, optionIdRequest);
                    //create submission answer
                    SubmissionAnswer submissionAnswer = new SubmissionAnswer();
                    submissionAnswer.setQuestion(optionalQuestion.get());
                    submissionAnswer.setQuizSubmission(quizSubmission);
                    submissionAnswer.setOption(option);
                    submissionAnswer = submissionAnswerRepo.save(submissionAnswer);
                    quizSubmission.getAnswers().add(submissionAnswer);
                    //
                    if (option == null) {
                        tmpScore = 0;
                        break;
                    } else {
                        if (option.getScore() > 0) {
                            tmpScore += option.getScore();
                        } else {
                            tmpScore = 0;
                            break;
                        }
                    }
                }
                totalScore += tmpScore;
            }
        }
        quizSubmission.setScore(totalScore);
        quizSubmission = quizSubmissionRepo.save(quizSubmission);
        QuizSubmitResultResponse quizSubmitResultResponse = new QuizSubmitResultResponse(quizSubmission);
        return ResponseEntity.ok(Map.of("score", totalScore, "request", quizSubmitResultResponse));
    }

    /**
     * @return QuizSubmissionResultResponse
     */
    public ResponseEntity<?> getAllSubmittedQuizzes() {
        List<QuizSubmission> quizSubmissions = quizSubmissionRepo.findAllByUser(userRepo.getById(getCurrentUid()));
        List<QuizSubmitResultResponse> rsp = quizSubmissions.stream().map(s -> new QuizSubmitResultResponse(s)).collect(Collectors.toList());
        return ResponseEntity.ok(rsp);
    }

    public Option getOptionDto(Question question, Long optionId) {
        QuestionDto questionDto = QuestionMapper.toDto(question);
        OptionDto optionDto = null;
        for (int i = 0; i < questionDto.getOptions().size(); i++) {
            optionDto = questionDto.getOptions().get(i);
            if (optionDto.getId().equals(optionId)) {
                return question.getOptions().get(i);
            }
        }
        return null;
    }

    long getCurrentUid() {
        MyUserDetails principal = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUser().getUid();
    }
}
