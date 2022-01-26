package com.nkd.quizmaker.helper;

import com.nkd.quizmaker.enumm.EAnswerType;
import com.nkd.quizmaker.enumm.EQuizStatus;
import com.nkd.quizmaker.enumm.EQuizType;
import com.nkd.quizmaker.mapper.QuestionMapper;
import com.nkd.quizmaker.mapper.QuizMapper;
import com.nkd.quizmaker.model.*;
import com.nkd.quizmaker.repo.QuizRepository;
import com.nkd.quizmaker.request.*;
import com.nkd.quizmaker.response.BaseResponse;
import com.nkd.quizmaker.response.QuestionResponse;
import com.nkd.quizmaker.response.QuizOverview;
import com.nkd.quizmaker.service.*;
import com.nkd.quizmaker.utils.QuizUtils;
import com.nkd.quizmaker.utils.ValidatorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("QuizHelper")
@RequiredArgsConstructor
@Slf4j
public class QuizHelper {

    private final QuizService quizService;
    private final UserService userService;
    private final QuestionService questionService;
    private final OptionService optionService;
    private final SubjectService subjectService;
    private final AssignmentServiceHelper assignmentServiceHelper;
    private final QuizRepository quizRepo;

    /**
     * get quizzes
     *
     * @param params
     * @return
     */
    public ResponseEntity<?> getQuizzes(Map<String, String> params) {
        //title
        String title = null;
        String paramTitle = params.get("title");
        if (!ValidatorUtils.isNullOrBlank(paramTitle)) {
            title = "%" + paramTitle + "%";
        }
        //subject
        String subject = null;
        String paramSubject = params.get("subject");
        if (!ValidatorUtils.isNullOrBlank(paramSubject)) {
            subject = "%" + paramSubject + "%";
        }
        //orderBy
        String sortBy = "create_date";
        String sortByParam = params.get("sortBy");
        if (!ValidatorUtils.isNullOrBlank(sortByParam)) {
            sortBy = sortByParam;
        }
        //direction
        String direction = "ASC";
        String directionParam = params.get("direction");
        if (!ValidatorUtils.isNullOrBlank(directionParam)) {
            direction = directionParam;
        }
        //page size
        int pageSize = 15;
        String paramPageSize = params.get("pageSize");
        if (ValidatorUtils.isNumeric(paramPageSize)) {
            pageSize = Integer.parseInt(paramPageSize);
        }
        //page count
        int page = 0;
        String pageParam = params.get("page");
        if (ValidatorUtils.isNumeric(pageParam)) {
            page = Integer.parseInt(pageParam);
        }

        int status = EQuizStatus.PUBLIC.getStatus();
        PageRequest pageable =
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));
        List<Quiz> quizzes = quizService.getQuizzesNative(subject, title, 1, status, pageable);
        List<QuizOverview> rs = quizzes.stream().map(q ->
                {
                    QuizOverview quizOverview = QuizMapper.toQuizOverview(q, quizService.getPlayedCount(q));
                    return quizOverview;
                }

        ).collect(Collectors.toList());
        return ResponseEntity.ok(new BaseResponse(rs, HttpStatus.OK.value()));
    }

    /**
     * find quiz by code
     *
     * @param code
     * @param params
     * @return
     */
    public ResponseEntity<?> findQuizByCode(String code, Map<String, String> params) {
        Integer status = 2;
        String paramStatus = params.get("status");
        if (!ValidatorUtils.isNullOrBlank(paramStatus) && ValidatorUtils.isNumeric(paramStatus)) {
            status = Integer.parseInt(paramStatus);
        }
        Integer active = 1;
        String paramActive = params.get("active");
        if (!ValidatorUtils.isNullOrBlank(paramActive) && ValidatorUtils.isNumeric(paramActive)) {
            active = Integer.parseInt(paramActive);
        }
        Quiz quiz = quizService.getQuizByCodeAndStatusNative(code, active, status);
        if (quiz == null) {
            return ResponseEntity.status(404)
                    .body(new BaseResponse(null, null, null, "Could not found quiz with code: " + code));
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(
                QuizMapper.toQuizOverview(quiz, quizService.getPlayedCount(quiz))
        );
        return ResponseEntity.ok(baseResponse);

    }

    /**
     * get quiz's questions
     */
    public ResponseEntity<?> getQuizQuestions(String code) {
        List<Question> questions = quizService.getQuizQuestions(code);
        if (questions == null || questions.isEmpty()) {
            return ResponseEntity.status(404).body(BaseResponse.notFound("Could not found quiz's questions with code: " + code));
        }
        List<QuestionResponse> rs = questions.stream().map(QuestionMapper::toQuestionResponse).collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.successData(rs));
    }


    /**
     * save quiz
     *
     * @param quizRequest
     * @return
     */
    public ResponseEntity<?> save(QuizRequest quizRequest) {
        Long quizRequestId = quizRequest.getId();
        String quizReqTitle = quizRequest.getTitle();

        Quiz quiz = null;
        if (quizRequestId != null && (quizService.getById(quizRequestId) != null)) {
            quiz = quizService.getById(quizRequestId);
        } else {
            quiz = new Quiz();
            quiz.setCode(generateCode());
            User user = userService.getCurrentUser();
            quiz.getOwners().add(user);
        }
        quiz.setTitle(quizReqTitle);
        quiz.setLimitTime(quizRequest.getLimitTime());
//        quiz.setActive(quizRequest.getac);
        EQuizType quizRequestType = quizRequest.getType();
        if (quizRequestType != null) {
            quiz.setQuizType(quizRequestType);
        } else {
            quiz.setQuizType(EQuizType.FUN);
        }
        int status = quizRequest.getStatus();

        EQuizStatus s = EQuizStatus.findByValue(status);
        if (s != null) {
            quiz.setStatus(s.getStatus());
        } else {
            quiz.setStatus(0);
        }
//        quizRequest.getStatus().getStatus();
        quiz = quizService.save(quiz);


        //subject
        SubjectRequest subjectRequest = quizRequest.getSubject();
        Subject subject;
        if (subjectRequest != null) {
            if (subjectRequest.getId() != null && (subject = subjectService.getById(subjectRequest.getId())) != null) {
                quiz.setSubject(subject);
            } else if (subjectRequest.getTitle() != null && (subject = subjectService.getByName(subjectRequest.getTitle())) != null) {
                quiz.setSubject(subject);
            } else {
                subject = new Subject();
                subject.setTitle(subjectRequest.getTitle());
                quiz.setSubject(subject);
                subject.getQuiz().add(quiz);
                subjectService.save(subject);
            }
        }
        quiz = quizService.save(quiz);

//
        List<QuestionRequest> questionRequests = quizRequest.getQuestions();
        if (questionRequests != null) {
            for (QuestionRequest questionRequest :
                    questionRequests) {
                Question question;
                if (questionRequest.getId() != null
                        && (question = questionService.getById(questionRequest.getId())) != null) {
                } else {
                    question = new Question();
                    /*

                     */
                    question.getQuizzes().add(quiz);

                }
                question.setOptionType(EAnswerType.MULTIPLE_CHOICE);
                question.setTitle(questionRequest.getTitle());
                question.setActive(1);
//                question.setOptionType(questionRequest.getOptionType());
                question = questionService.save(question);
                /*

                 */

                for (OptionRequest optionRequest :
                        questionRequest.getOptions()) {
                    Option option;
                    if (optionRequest.getId() != null
                            && (option = optionService.getById(optionRequest.getId())) != null) {
                    } else {
                        option = new Option();
                        question.getOptions().add(option);
                        option.getQuestions().add(question);
                    }
                    option.setContent(optionRequest.getContent());
                    if (optionRequest.getScore() != null) {
                        option.setScore(optionRequest.getScore());
                    } else {
                        option.setScore(0.0);
                    }
                    option.setFExplanation(optionRequest.getFExplanation());
                    option.setTExplanation(optionRequest.getTExplanation());
                    option = optionService.save(option);

                }
            }
        }
        Long savedQuizId = quiz.getId();
        Quiz respData = quizService.getById(savedQuizId);
        Map<String, Object> a = QuizMapper.toQuizDetailsResponse(respData);
        return ResponseEntity.ok(BaseResponse.successData(
                a
                , "Update quiz success!"));
    }

    @Transactional
    public String generateCode() {
        String code = QuizUtils.createRandomCode();
        while (quizService.getByCode(code) != null) {
            code = QuizUtils.createRandomCode();
        }
        return code;
    }

    public ResponseEntity<?> removeQuestion(Long questionId) {
        Question question = questionService.getById(questionId);
        if (question != null) {
            question.setActive(0);
            questionService.save(question);
            return ResponseEntity.ok().body(BaseResponse.successData(null, "Remove question with id: " + questionId + " success"));
        }
        return ResponseEntity.badRequest().body(BaseResponse.badRequest("Can not find question with id : " + questionId));
    }


}
