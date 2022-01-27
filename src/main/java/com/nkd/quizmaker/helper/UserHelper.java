package com.nkd.quizmaker.helper;

import com.nkd.quizmaker.enumm.EAnswerType;
import com.nkd.quizmaker.enumm.EQuizType;
import com.nkd.quizmaker.enumm.ESubmitType;
import com.nkd.quizmaker.mapper.*;
import com.nkd.quizmaker.model.*;
import com.nkd.quizmaker.request.*;
import com.nkd.quizmaker.response.*;
import com.nkd.quizmaker.service.*;
import com.nkd.quizmaker.utils.MyJWTUtils;
import com.nkd.quizmaker.utils.ValidatorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Component("UserHelper")
@Slf4j
@RequiredArgsConstructor
public class UserHelper {

    private final UserService userService;
    private final QuizService quizService;
    private final MailService mailService;
    private final QuestionService questionService;
    private final OptionService optionService;
    private final FileStorageService fileStorageService;
    private final QuizSubmissionService quizSubmissionService;
    private final AssignmentService assignmentService;
    private final QuizSubmisionAnswerService answerSubmitService;
    private final PasswordEncoder passwordEncoder;
    private final StreakService streakService;


    public ResponseEntity<?> submitFunQuiz(QuizSubmissionRequest req) {
        Long quizReqId = req.getQuizId();
        Quiz quiz = quizService.getById(quizReqId);
        if (quiz == null)
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("Quiz is not exist!"));
//        //exam quiz
        int attemptCount = 1;
        List<QuizSubmission> submit = userService.getCurrentUserSubmissionQuiz(quizReqId);

        if (quiz.getQuizType().equals(EQuizType.EXAM) && submit != null) {
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("You already submit this quiz"));
        } else {
            if (submit != null && !submit.isEmpty()) {
                attemptCount = submit.get(0).getAttempt() + 1;
            }
        }
        //

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("quizTitle", quiz.getTitle());
        resp.put("quizId", quiz.getId());
        List<Object> answers = new LinkedList<>();
        double score = 0;
        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setUser(userService.getCurrentUser());
        quizSubmission.setQuiz(quiz);
        quizSubmission.setAttempt(attemptCount);
        quizSubmission.setSubmissionType(ESubmitType.FUN);
        quizSubmission = quizSubmissionService.save(quizSubmission);
        int submitQuestionsCount = 0;
        for (SubmissionAnswerRequest answerReq :
                req.getAnswers()) {
            Long reqQuestionId = answerReq.getQuestionId();
            Question question = questionService.getById(reqQuestionId);
            Map<String, Object> map = new LinkedHashMap<>();
            if (question != null) {
                EAnswerType optionType = question.getOptionType();
                Set<Long> options = null;
                map.put("questionId", reqQuestionId);
                map.put("questionTitle", question.getTitle());
                map.put("optionType", question.getOptionType().name());
                List<Object> list = new ArrayList<>();
                if (optionType.name().equals(EAnswerType.MULTIPLE_CHOICE.name())
                        || optionType.name().equals(EAnswerType.A_CHOICE.name())) {
                    options = answerReq.getOptions();
                    double optionScore = 0;
                    boolean isWrong = false;
                    for (Long optionId :
                            options) {
                        Option option = optionService.getById(optionId);
                        list.add(Map.of("optionId", option.getId(), "content", option.getContent()));
                        double c = option.getScore();
                        if (c <= 0) {
                            isWrong = true;
                        } else {
                            optionScore += c;
                            score += c;

                        }
                        SubmissionAnswer submissionAnswer = new SubmissionAnswer();

                        submissionAnswer.setQuestion(question);
                        submissionAnswer.setOption(option);
                        submissionAnswer.setQuizSubmission(quizSubmission);
                        submissionAnswer = answerSubmitService.save(submissionAnswer);
                        quizSubmission.getAnswers().add(submissionAnswer);

                    }
                    if (isWrong) {
                        score -= optionScore;
                        if (score < 0) {
                            score = 0;
                        }
                    }
                    map.put("answers", list);

                    //

                } else {
                    String optionText = answerReq.getOptionText();
                    list.add(Map.of("optionText", optionText));
                    SubmissionAnswer submissionAnswer = new SubmissionAnswer();
                    submissionAnswer.setQuestion(question);
                    submissionAnswer.setMessage(optionText);
                    submissionAnswer = answerSubmitService.save(submissionAnswer);
                    quizSubmission.getAnswers().add(submissionAnswer);

                }
                submitQuestionsCount++;
            }
        }
        quizSubmission.setScore(score);
        quizSubmission.setStartTime(req.getStartTime());
        quizSubmission.setFinishTime(req.getFinishTime());
        quizSubmission.setCompleteCount(submitQuestionsCount);

        resp.put("score", score);
        resp.put("submitQuestionsCount", submitQuestionsCount);
        resp.put("startTime", req.getStartTime());
        resp.put("finishTime", req.getFinishTime());
        resp.put("numOfQuestions", quiz.getQuestions().size());

        double quizScore = 0;
        List<Question> quizQuestions = quizService.getQuizQuestions(quiz, 1);
        for (Question question :
                quizQuestions) {
            for (Option option :
                    question.getOptions()) {
                quizScore += option.getScore();
            }
        }

        resp.put("quizScore", quizScore);
        resp.put("answers", answers);
        resp.put("attempt", attemptCount);
        quizSubmissionService.save(quizSubmission);


        return ResponseEntity.ok(BaseResponse.successData(resp, "Submit answers success! "));
    }


    /**
     * registration member account
     *
     * @param req
     * @return
     */
    public ResponseEntity<?> registration(RegistrationRequest req) {
        String usernameReq = req.getUsername().trim();
        String emailReq = req.getEmail().trim();
        String passReq = req.getPassword().trim();
        String fullNameReq = req.getFullName().trim();
        Integer genderReq = req.getGender();
        //check exist username
        if (userService.getByUsername(usernameReq) != null)
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("Username : " + usernameReq + " has already exist!"));
        //check exist email
        if (userService.getByEmail(emailReq) != null)
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("Email : " + emailReq + " has already exist!"));
        User user = new User();
        user.setUsername(usernameReq);
        user.setEmail(emailReq);
        user.setPassword(passwordEncoder.encode(passReq));
        user.setFullName(fullNameReq);
        user.setGender(genderReq);
        user.setActive(0);
        user.getRoles().add(userService.getMemberRole());
        //send verification code
        try {
            String code = mailService.sendVerificationCode(emailReq);
            user.setVerificationCode(code);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        userService.save(user);
        return ResponseEntity.ok(BaseResponse.successData(null, "Save user success"));
    }

    /**
     * verification account
     *
     * @param code
     * @return
     */
    public ResponseEntity<?> verification(String code) {
        User user = userService.getByVerificationCode(code);
        if (user != null) {
            user.setVerificationCode(null);
            user.setActive(1);
            userService.save(user);
            return ResponseEntity.ok(BaseResponse.successData(Map.of(
                    "accessToken", MyJWTUtils.createToken(user.getEmail(), user.getRoles().stream().map(Role::getName).collect(Collectors.toList())),
                    "refreshToken", MyJWTUtils.createRefreshToken(user.getEmail())
            ), "Active user success!"));
        }
        return ResponseEntity.badRequest().body(BaseResponse.badRequest("Code is not valid"));
    }

    /**
     * validation input field
     *
     * @param input
     * @param value
     * @return
     */
    public ResponseEntity<?> validationInput(String input, String value) {
        input = input.trim();
        value = value.trim();
        //check email input
        if (!ValidatorUtils.isNullOrBlank(input) && input.equals("email")) {
            if (ValidatorUtils.isEmail(value)) {
                if (userService.existByEmail(value))
                    return ResponseEntity.badRequest().body(BaseResponse.badRequest("Email has already been used!"));
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body(BaseResponse.badRequest("Email is not valid"));
            }
        }
        if (!ValidatorUtils.isNullOrBlank(input) && input.equals("username")) {
            if (!ValidatorUtils.isNullOrBlank(value) && value.length() >= 4) {
                if (userService.existByUsername(value))
                    return ResponseEntity.badRequest().body(BaseResponse.badRequest("Username has already been used!"));
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body(BaseResponse.badRequest("Username is not valid"));
            }
        }
        if (!ValidatorUtils.isNullOrBlank(input) && input.equals("phone")) {
            if (ValidatorUtils.isPhoneNumber(value)) {
                if (userService.existByPhone(value))
                    return ResponseEntity.badRequest().body(BaseResponse.badRequest("Phone has already been used!"));
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body(BaseResponse.badRequest("Phone number is not valid"));
            }
        }
        return ResponseEntity.ok().build();

    }

    /**
     * get current user detail
     *
     * @return
     */
    public ResponseEntity<?> getCurrentUserDetail() {
        User user = userService.getCurrentUser();
        Map<String, Object> rs = new HashMap<>();
        rs.put("id", user.getUid());
        rs.put("username", user.getUsername());
        rs.put("fullName", user.getFullName());
        rs.put("email", user.getEmail());
        rs.put("avt", user.getAvtImgUrl());
        rs.put("gender", user.getGender());
        rs.put("phone", user.getPhone());
        rs.put("streaks", streakService.getTopStreak(userService.getCurrentUser()).getCount());
//        List<QuizSubmission> submissions = quizSubmissionService.getSubmissions(user);
//        List<QuizSubmissionResponse> joinedQuizzes = submissions.stream()
//                .map(QuizMapper::toQuizSubmissionResponse)
//                .collect(Collectors.toList());
//        rs.put("joinedQuizzes", joinedQuizzes);
        return ResponseEntity.ok(BaseResponse.successData(rs, "Get user detail success!"));
    }

    /**
     * update user avt
     */
    public ResponseEntity<?> updateAvt(MultipartFile file) {
        User user = userService.getCurrentUser();
        if (file != null & ValidatorUtils.isImage(file)) {
            String name = fileStorageService.storeFile(file, user.getUsername());
            user.setAvtImgUrl(name);
            userService.save(user);
            return ResponseEntity.ok(BaseResponse.successData(Map.of("avt", name), "Update avatar success!"));
        }
        return ResponseEntity.badRequest().body(BaseResponse.badRequest("File is not valid"));
    }

    /**
     * update user detail
     *
     * @param req
     * @return
     */
    public ResponseEntity<?> updateUserDetail(UpdateUserRequest req) {
        User user = userService.getCurrentUser();
        String username = req.getUsername();
        if (username != null && username.trim().length() >= 6)
            user.setUsername(username);
        String fullName = req.getFullName();
        if (fullName != null && fullName.trim().length() >= 6)
            user.setFullName(fullName);
        String phone = req.getPhone();
        if (phone != null && ValidatorUtils.isPhoneNumber(phone)) {
            user.setPhone(phone);
        }
        userService.save(user);
        return ResponseEntity.ok(BaseResponse.successData(username, "Update user detail success!"));
    }


    /**
     * get user's quizzes
     *
     * @param params
     * @return
     */
    public ResponseEntity<?> getUserQuizzes(Map<String, String> params) {
        //orderBy
        String sortBy = "create_date";
        String sortByParam = params.get("sortBy");
        if (!ValidatorUtils.isNullOrBlank(sortByParam)) {
            sortBy = sortByParam;
        }
        //direction
        String direction = "DESC";
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
        //status
        Integer status = null;
        String statusParam = params.get("status");
        if (ValidatorUtils.isNumeric(statusParam)) {
            status = Integer.parseInt(statusParam);
        }
        //active
        Integer active = null;
        String activeParam = params.get("active");
        if (ValidatorUtils.isNumeric(activeParam)) {
            active = Integer.parseInt(activeParam);
        }

        PageRequest pageable =
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));

        List<Quiz> quizzes = userService.getUserQuizzes(status, active, pageable);
        List<QuizOverview> data = quizzes.stream().map(q -> {
            QuizOverview quizOverview = QuizMapper.toQuizOverview(q, quizService.getPlayedCount(q));
            return quizOverview;

        }).collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.successData(data, "Get user quizzes success!"));
    }

    /**
     * get submitted quizzes
     *
     * @return
     */
    public ResponseEntity<?> getSubmittedQuizzes() {
        List<QuizSubmission> submissionQuizzes =
                userService.getSubmissionQuizzes();
        List<QuizSubmissionResponse> rs = submissionQuizzes.stream()
                .map(QuizMapper::toQuizSubmissionResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.successData(rs));
    }

    /**
     * search users
     *
     * @param params
     * @return
     */
    public ResponseEntity<?> searchUsers(Map<String, String> params) {
        //username
        String username = null;
        String paramTitle = params.get("username");
        if (!ValidatorUtils.isNullOrBlank(paramTitle)) {
            username = "%" + paramTitle + "%";
        }
        //fullName
        String fullName = null;
        String paramFullName = params.get("fullName");
        if (!ValidatorUtils.isNullOrBlank(paramFullName)) {
            fullName = "%" + paramFullName + "%";
        }
        String email = null;
        String paramEmail = params.get("email");
        if (!ValidatorUtils.isNullOrBlank(paramEmail)) {
            email = "%" + paramEmail + "%";
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
        PageRequest pageable =
                PageRequest.of(page, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));

        List<User> users = userService.searchUsers(username, email, fullName, pageable);
        List<UserOverviewResponse> rs = users.stream()
                .map(UserMapper::toUserOverviewResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(BaseResponse.successData(rs, "Search users success!"));
    }


    public ResponseEntity<?> refreshToken(String token) {
        return null;
    }

    /**
     * get assign quizzes
     *
     * @return
     */
    public ResponseEntity<?> getAssigmentQuizzes() {
        User currentUser = userService.getCurrentUser();
        List<AssignmentInfo> assignmentInfos = assignmentService.getAssignmentInfos(currentUser, 1);
        List<Map<String, Object>> data = assignmentInfos.stream()
                .map(a -> {
                    return AssignmentMapper.toAssignmentInfoResponse(a, quizService.getPlayedCount(a.getQuiz()));
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.successData(data));
    }

    /**
     * get assigned quizzes
     *
     * @return
     */
    public ResponseEntity<?> getAssignedQuizzes() {
        List<Assignment> assignments =
                assignmentService.getAssignedQuizzes(userService.getCurrentUser(), 1);
        List<Map<String, Object>> data = assignments.stream()
                .map(AssignmentMapper::toAssignmentResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.successData(data));
    }

    public ResponseEntity<?> getAssignedQuiz(Long quizId) {
        User currentUser = userService.getCurrentUser();
        Assignment assignment = assignmentService.getAssignmentByIdAndAssignedUser(new Assignment.QuizAssignmentId(currentUser.getUid(), quizId), currentUser);
        if (assignment == null)
            return ResponseEntity.notFound().build();
        Map<String, Object> data = AssignmentMapper.toAssignmentResponse(assignment);
        Quiz quiz = assignment.getQuiz();
        double quizScore = quizService.getQuizScore(quiz);
        QuizOverview quizInfo = QuizMapper.toQuizOverview(quiz, quizScore);
        data.put("quiz", quizInfo);
        return ResponseEntity.ok(BaseResponse.successData(data));

    }

    public ResponseEntity<?> getAssignedQuizDetail(String code) {
        Quiz quiz = quizService.getQuizByCodeAndStatusNative(code, 1, 1);
        Assignment assignment = assignmentService.getByUserAndQuiz(userService.getCurrentUser(), quiz, 1);
        if (assignment == null) {
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("You have not assigned this quiz"));
        }
        QuizOverview quizOverview = quizService.getAssignedQuizDetail(code);
        if (quizOverview != null) {
            return ResponseEntity.ok(BaseResponse.successData(quizOverview));
        }
        return ResponseEntity.badRequest().body(BaseResponse.badRequest("Could not find quiz with code: " + code));

    }


    /**
     * get assignment quiz report
     */
    public ResponseEntity<?> getAssignmentReport(Long id) {
        Map<String, Object> reportDetailResponse = new HashMap<>();
        AssignmentInfo assignmentInfo = assignmentService.getAssignmentInfos(id);
        if (assignmentInfo == null) {
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("Could not found assignment info with id: " + id));
        }
        Quiz quiz = assignmentInfo.getQuiz();
        List<Assignment> assignments = assignmentService.getAssignmentByInfo(assignmentInfo);
        reportDetailResponse.put("assignmentInfo", AssignmentMapper.toAssignmentInfoResponse(assignmentInfo, 0));
        List<Object> users = new ArrayList<>();
        for (Assignment assignment :
                assignments) {
            Map<String, Object> userEntryMap = new HashMap<>();
            User user = assignment.getUser();
            userEntryMap.put("userInfo", UserMapper.toUserOverviewResponse(user));
            userEntryMap.put("submitDate", assignment.getCreatedDate());
            List<Object> userEntryAnswersMap = new ArrayList<>();
            List<QuizSubmission> userSubmissionQuiz = quizSubmissionService.getByUserAndSubmitType(user, quiz, 1, ESubmitType.EXAM);
            if (userSubmissionQuiz != null && !userSubmissionQuiz.isEmpty()) {
                userEntryMap.put("complete", true);
            } else {
                userEntryMap.put("complete", false);
            }
            for (QuizSubmission quizSubmission :
                    userSubmissionQuiz) {
                Double score = quizSubmission.getScore();
                userEntryMap.put("score", score);
                Date createDate = quizSubmission.getCreateDate();
                userEntryMap.put("submissionDate", createDate);
                userEntryMap.put("completeCount", quizSubmission.getCompleteCount());
                List<SubmissionAnswer> answers = quizSubmission.getAnswers();
                for (SubmissionAnswer answer :
                        answers) {
                    Map<String, Object> userAnswerOptionsMap = new HashMap<>();
                    Question question = answer.getQuestion();
                    userAnswerOptionsMap.put("title", question.getTitle());
                    userAnswerOptionsMap.put("optionType", question.getOptionType().name());
                    userAnswerOptionsMap.put("id", question.getId());
                    List<SubmissionAnswer> submitAnswer = quizSubmissionService.getSubmitAnswer(quizSubmission, question);
                    List<Object> options = submitAnswer.stream().map(submissionAnswer -> {
                        Option option = submissionAnswer.getOption();
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("id", option.getId());
                        map2.put("content", option.getContent());
                        String message = submissionAnswer.getMessage();
                        map2.put("message", message);
                        return map2;
                    }).collect(Collectors.toList());
                    userAnswerOptionsMap.put("options", options);
                    userEntryAnswersMap.add(userAnswerOptionsMap);
                }

            }
            userEntryMap.put("answers", userEntryAnswersMap);
            users.add(userEntryMap);
        }
        reportDetailResponse.put("users", users);

        return ResponseEntity.ok(BaseResponse.successData(reportDetailResponse));
    }

    /**
     * assign to users
     *
     * @param assignmentReq
     * @return
     */
    public ResponseEntity<?> assignToUsers(AssignmentRequest assignmentReq) {
        Set<String> emails = assignmentReq.getEmails();
        Long aInfoId = assignmentReq.getId();
        Date startDate = assignmentReq.getStartDate();
        Date finishDate = assignmentReq.getFinishDate();
        String aTitle = assignmentReq.getTitle();
        String description = assignmentReq.getDescription();

        boolean isUpdate = false;
        AssignmentInfo info;
        if (aInfoId != null && ValidatorUtils.isNumeric(String.valueOf(aInfoId)
        ) && (info = assignmentService.getAssignmentInfos(aInfoId)) != null) {
            isUpdate = true;
        } else {
            info = new AssignmentInfo();
        }
        info.setStartDate(startDate);
        info.setFinishDate(finishDate);
        info.setDescription(description);
        info.setTitle(aTitle);
        info.setStatus(1);

        if (emails != null && !emails.isEmpty()) {
            Long quizId = assignmentReq.getQuizId();
            if (isUpdate) {
                quizId = info.getQuiz().getId();
            }
            Quiz quiz = quizService.getByIdAndUser(quizId, quizService.getCurrentUser());
            if (quiz == null) {
                return ResponseEntity.badRequest().body(BaseResponse.badRequest("Quiz with id: " + quizId + " is not exist!"));
            }
            quiz.setStatus(1);
            quizService.save(quiz);
            info.setQuiz(quiz);
            info = assignmentService.saveInfo(info);
            for (String email :
                    emails) {
                User user = userService.getByEmail(email);
                if (user != null) {
                    Assignment assignment = assignmentService.getByUserAndQuiz(user, quiz, 1);
                    String fullName = user.getFullName();
                    String username = user.getUsername();
                    try {
                        if (assignment != null && isUpdate) {
                            List<QuizSubmission> quizSubmissions = quizSubmissionService.getByUserAndSubmitType(user, quiz, 1, ESubmitType.EXAM);
                            for (int i = 0; i < quizSubmissions.size(); i++) {
                                QuizSubmission quizSubmission = quizSubmissions.get(i);
                                quizSubmission.setStatus(0);
                                quizSubmissionService.save(quizSubmission);

                            }
                            mailService
                                    .sendAssignUpdateToUser(username == null ? fullName : username, email, quiz.getTitle(), quiz.getCode(), startDate, finishDate, new Date());
                        } else {
                            Assignment.QuizAssignmentId id = new Assignment.QuizAssignmentId(user.getUid(), quiz.getId());
                            assignment = new Assignment();
                            assignment.setId(id);
                            user.getAssignedQuizzes().add(assignment);
                            mailService.sendAssignQuizToUser(username == null ? fullName : username, email, quiz.getTitle(), quiz.getCode(), startDate, finishDate);
                        }
                        assignment.setAssignmentInfo(info);
                        assignment.setUser(user);
                        assignment.setQuiz(quiz);
                        assignment.setActive(1);
                        assignment.setStatus(0);

                        assignmentService.save(assignment);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            }
            return ResponseEntity.ok(BaseResponse.successData(null, "Assign quiz to users success!"));
        }
        return ResponseEntity.badRequest().body(BaseResponse.badRequest("Assigned emails is empty!"));
    }

    /**
     * change password request
     *
     * @return
     */
    public ResponseEntity<?> changePasswordRequest() {
        User user = userService.getCurrentUser();
        String code = userService.generateCode();
        user.setVerificationCode(code.toLowerCase());
        try {
            mailService.sendPasswordChangeRequestCode(user.getEmail(), code);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        userService.save(user);
        return ResponseEntity.ok("Change password request success!");
    }

    /**
     * change password
     *
     * @param req
     * @return
     */
    public ResponseEntity<?> changePassword(ChangePasswordRequest req) {

        User user = userService.getByVerificationCode(req.getVerificationCode().toLowerCase().trim());
        if (user == null)
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("Verification code is not valid!"));

        user.setVerificationCode(null);
        String oldPassword = req.getOldPassword().trim();
        String newPassword = req.getNewPassword().trim();
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("Old password does not matches!"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);
        return ResponseEntity.badRequest().body(BaseResponse.badRequest("Update password success!"));

    }

    public ResponseEntity<?> getUserQuiz(Long id) {
        Quiz quiz = quizService.getByIdAndUser(id, userService.getCurrentUser());
        if (quiz == null)
            return ResponseEntity.ok(BaseResponse.badRequest("Quiz with id : " + id + " does not exist!"));
        Map<String, Object> rs = QuizMapper.toQuizDetailsResponse(quiz);
        return ResponseEntity.ok(BaseResponse.successData(rs, "Get quiz with id: " + id + " success!"));
    }

    public ResponseEntity<?> removeQuiz(Long id, Integer active) {
        Quiz quiz = quizService.getByIdAndUser(id, userService.getCurrentUser());
        if (quiz != null) {
            if (active != null && active == 0) {
                quiz.setActive(active);
            } else {
                quiz.setActive(1);
            }
            quizService.save(quiz);
            return ResponseEntity.ok(BaseResponse.successData(null, "Remove quiz with id: " + id + " success!"));
        }
        return ResponseEntity.status(401).body(BaseResponse.badRequest("Could not found quiz witg id: " + id));

    }

    public ResponseEntity<?> submitExamQuiz(QuizSubmissionRequest req) {
        //kiem tra quiz ton tai (exam) va chua submit lan nao
        Long quizId = req.getQuizId();
        //1 : private status
        Quiz quiz = quizService.getByIdAndActiveAndStatus(quizId, 1, 1);
        if (quiz == null) {
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("Could not found quiz with id: " + quizId));
        }
        User currentUser = userService.getCurrentUser();
        Assignment assignment = assignmentService.getByUserAndQuiz(currentUser, quiz, 1);
        if (assignment == null) {
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("Could not found assignment"));
        }
        List<QuizSubmission> quizSubmissions = quizSubmissionService.getByUserAndSubmitType(currentUser, quiz, 1, ESubmitType.EXAM);
        if (quizSubmissions != null && !quizSubmissions.isEmpty()) {
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("You has been submit this quiz"));
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setAttempt(1);
        quizSubmission.setUser(userService.getCurrentUser());
        quizSubmission.setSubmissionType(ESubmitType.EXAM);
        quizSubmission.setQuiz(quiz);
        quizSubmission.setStatus(1);
        quizSubmission = quizSubmissionService.save(quizSubmission);
        double score = 0.0;
        int submitQuestionsCount = 0;
        for (SubmissionAnswerRequest answerReq :
                req.getAnswers()) {
            Long reqQuestionId = answerReq.getQuestionId();
            Question question = questionService.getById(reqQuestionId);
            Map<String, Object> map = new LinkedHashMap<>();
            if (question != null) {
                EAnswerType optionType = question.getOptionType();
                Set<Long> options = null;
                map.put("questionId", reqQuestionId);
                map.put("questionTitle", question.getTitle());
                map.put("optionType", question.getOptionType().name());
                List<Object> list = new ArrayList<>();
                if (optionType.name().equals(EAnswerType.MULTIPLE_CHOICE.name())
                        || optionType.name().equals(EAnswerType.A_CHOICE.name())) {
                    options = answerReq.getOptions();
                    double optionScore = 0;
                    boolean isWrong = false;
                    for (Long optionId :
                            options) {
                        Option option = optionService.getById(optionId);
                        list.add(Map.of("optionId", option.getId(), "content", option.getContent()));
                        double c = option.getScore();
                        if (c <= 0) {
//                            score -= optionScore;
                            isWrong = true;
//                            break;
                        } else {
                            optionScore += c;
                            score += c;

                        }
                        SubmissionAnswer submissionAnswer = new SubmissionAnswer();

                        submissionAnswer.setQuestion(question);
                        submissionAnswer.setOption(option);
                        submissionAnswer.setQuizSubmission(quizSubmission);
                        submissionAnswer = answerSubmitService.save(submissionAnswer);
                        quizSubmission.getAnswers().add(submissionAnswer);

                    }
                    if (isWrong) {
                        score -= optionScore;
                        if (score < 0) {
                            score = 0;
                        }
                    }
                    map.put("answers", list);

                    //

                } else {
                    String optionText = answerReq.getOptionText();
                    list.add(Map.of("optionText", optionText));
                    SubmissionAnswer submissionAnswer = new SubmissionAnswer();
                    submissionAnswer.setQuestion(question);
                    submissionAnswer.setMessage(optionText);
                    submissionAnswer = answerSubmitService.save(submissionAnswer);
                    quizSubmission.getAnswers().add(submissionAnswer);

                }
                submitQuestionsCount++;
            }
        }
        quizSubmission.setScore(score);
        quizSubmission.setStartTime(req.getStartTime());
        quizSubmission.setFinishTime(req.getFinishTime());
        quizSubmission.setCompleteCount(submitQuestionsCount);

        resp.put("score", score);
        resp.put("submitQuestionsCount", submitQuestionsCount);
        resp.put("startTime", req.getStartTime());
        resp.put("finishTime", req.getFinishTime());
        resp.put("numOfQuestions", quiz.getQuestions().size());

        double quizScore = 0;
        List<Question> quizQuestions = quizService.getQuizQuestions(quiz, 1);
        for (Question question :
                quizQuestions) {
            for (Option option :
                    question.getOptions()) {
                quizScore += option.getScore();
            }
        }

        resp.put("quizScore", quizScore);
        resp.put("attempt", 1);
        quizSubmission.setStatus(1);
        quizSubmissionService.save(quizSubmission);
        assignment.setStatus(1);
        assignmentService.save(assignment);

        return ResponseEntity.ok(BaseResponse.successData(resp, "Submit exam answers success!"));

    }

    public ResponseEntity<?> getSubmissionAnswer(String code) {
        Quiz quiz = quizService.getByCodeAndActiveAndStatus(code, 1, 1);
        if (quiz == null) {
            return ResponseEntity.badRequest().build();
        }
        List<QuizSubmission> quizSubmissions = quizSubmissionService
                .getByUserAndSubmitType(userService.getCurrentUser(), quiz, 1, ESubmitType.EXAM);
        if (quizSubmissions != null && !quizSubmissions.isEmpty()) {
            QuizSubmission s = quizSubmissions.get(0);
            Double score = s.getScore();
            Date startTime = s.getStartTime();
            Date finishTime = s.getFinishTime();
            int completeCount = s.getCompleteCount();
            Map<String, Object> map = new HashMap<>();
            map.put("startTime", startTime);
            map.put("status", s.getStatus());
            map.put("finishTime", finishTime);
            map.put("completeCount", completeCount);
            map.put("score", score);
            List<Object> answersList = new ArrayList<>();
            for (SubmissionAnswer answer :
                    s.getAnswers()) {
                Map<String, Object> userAnswerOptionsMap = new HashMap<>();
                Question question = answer.getQuestion();
                userAnswerOptionsMap.put("title", question.getTitle());
                userAnswerOptionsMap.put("optionType", question.getOptionType().name());
                userAnswerOptionsMap.put("id", question.getId());
                List<SubmissionAnswer> submitAnswer = quizSubmissionService.getSubmitAnswer(s, question);
                List<Object> list = new ArrayList<>();
                for (SubmissionAnswer a :
                        submitAnswer) {
                    Map<String, Object> optionMap = new HashMap<>();
                    optionMap.put("id", a.getOption().getId());
                    optionMap.put("content", a.getOption().getContent());
                    list.add(optionMap);
                }
                userAnswerOptionsMap.put("options", list);
                answersList.add(userAnswerOptionsMap);
            }
            map.put("answers", answersList);
            map.put("isComplete", true);
            map.put("submitQuestionsCount", s.getCompleteCount());
            map.put("attempt", s.getAttempt());
            map.put("quizScore", quizService.getQuizScore(quiz));
            map.put("numOfQuestions", quiz.getQuestions().size());
            return ResponseEntity.ok().body(BaseResponse.successData(map));
        } else {
            return ResponseEntity.ok().body(BaseResponse.successData(Map.of("isComplete", false)));

        }
    }

    public ResponseEntity<?> getAssignedUsers(Long assignInfoId) {

        AssignmentInfo info = assignmentService.getAssignmentInfos(assignInfoId);
        if (info == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Assignment> assignments = assignmentService.getAssignmentByInfo(info);
        List<UserOverviewResponse> data = assignments.stream()
                .map(a -> {
                    User user = a.getUser();
                    UserOverviewResponse userOverviewResponse = UserMapper.toUserOverviewResponse(user);
                    return userOverviewResponse;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.successData(data));
    }

    public ResponseEntity<?> getExamSubmitAnswers(Long quizId, Long uid) {
        Quiz quiz = quizService.getByIdAndActiveAndStatus(quizId, 1, 1);
        if (quiz == null)
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("Could not found quiz with id: " + quizId));
        User user = userService.getById(uid);
        List<QuizSubmission> quizSubmissions = quizSubmissionService.getByUserAndSubmitType(user, quiz, 1, ESubmitType.EXAM);

        QuizSubmission quizSubmission = quizSubmissions.get(0);
        List<Object> answersList = new ArrayList<>();
        for (SubmissionAnswer answer :
                quizSubmission.getAnswers()) {
            Map<String, Object> userAnswerOptionsMap = new HashMap<>();
            Question question = answer.getQuestion();
            userAnswerOptionsMap.put("title", question.getTitle());
            userAnswerOptionsMap.put("optionType", question.getOptionType().name());
            userAnswerOptionsMap.put("id", question.getId());
            List<SubmissionAnswer> submitAnswer = quizSubmissionService.getSubmitAnswer(quizSubmission, question);
            List<Object> list = new ArrayList<>();
            for (SubmissionAnswer a :
                    submitAnswer) {
                Map<String, Object> optionMap = new HashMap<>();
                optionMap.put("id", a.getOption().getId());
                optionMap.put("content", a.getOption().getContent());
                list.add(optionMap);
            }
            userAnswerOptionsMap.put("options", list);
            answersList.add(userAnswerOptionsMap);
        }
        return ResponseEntity.ok(BaseResponse.successData(answersList));

    }
}
