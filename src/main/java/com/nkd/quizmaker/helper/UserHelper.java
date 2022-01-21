package com.nkd.quizmaker.helper;

import com.nkd.quizmaker.enumm.EAnswerType;
import com.nkd.quizmaker.enumm.EQuizType;
import com.nkd.quizmaker.mapper.QuizMapper;
import com.nkd.quizmaker.mapper.UserMapper;
import com.nkd.quizmaker.model.*;
import com.nkd.quizmaker.request.*;
import com.nkd.quizmaker.response.*;
import com.nkd.quizmaker.service.*;
import com.nkd.quizmaker.utils.MyJWTUtils;
import com.nkd.quizmaker.utils.ValidatorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component("UserHelper")
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


    public ResponseEntity<?> submissionAnswers(QuizSubmissionRequest req) {
        Long quizReqId = req.getQuizId();
        Quiz quiz = quizService.getById(quizReqId);
        if (quiz == null)
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("Quiz is not exist!"));
//        //exam quiz
        List<QuizSubmission> submit = userService.getCurrentUserSubmissionQuiz(quizReqId);
        if (quiz.getQuizType().equals(EQuizType.EXAM) && submit != null)
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("You already submit this quiz"));

        //
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("quizTitle", quiz.getTitle());
        resp.put("quizId", quiz.getId());
        List<Object> answers = new LinkedList<>();
        double score = 0;
        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setUser(userService.getCurrentUser());
        quizSubmission.setQuiz(quiz);
        quizSubmission = quizSubmissionService.save(quizSubmission);
        int submitQuestionsCount = 0;
        for (SubmissionAnswerRequest answerReq :
                req.getAnswers()) {
            Long reqQuestionId = answerReq.getQuestionId();
            Question question = questionService.getById(reqQuestionId);
            Map<String, Object> map = new LinkedHashMap<>();
            if (question != null) {
                SubmissionAnswer submissionAnswer = new SubmissionAnswer();
                submissionAnswer.setQuestion(question);
                EAnswerType optionType = question.getOptionType();
                Set<Long> options = null;
                map.put("questionId", reqQuestionId);
                map.put("questionTitle", question.getTitle());
                map.put("optionType", question.getOptionType().name());
                List<Object> list = new ArrayList<>();
                if (optionType.equals(EAnswerType.MULTIPLE_CHOICE)
                        || optionType.equals(EAnswerType.A_CHOICE)) {
                    options = answerReq.getOptions();
                    double optionScore = 0;
                    for (Long optionId :
                            options) {
                        Option option = optionService.getById(optionId);
                        list.add(Map.of("optionId", option.getId(), "content", option.getContent()));
                        double c = option.getScore();
                        if (c <= 0) {
                            score -= optionScore;
                            break;
                        } else {
                            optionScore += c;
                            score += c;

                        }
                        submissionAnswer.setOption(option);
                    }
                    map.put("answers", list);

                    //

                } else {
                    String optionText = answerReq.getOptionText();
                    list.add(Map.of("optionText", optionText));
                    submissionAnswer.setMessage(optionText);
                }
                //track user behavior
                if (submissionAnswer.getOption() != null) {
                    submissionAnswer.setQuizSubmission(quizSubmission);
                    quizSubmission.getAnswers().add(submissionAnswer);
                    answerSubmitService.save(submissionAnswer);
                    submitQuestionsCount++;
                    answers.add(map);
                }
            }
        }
        quizSubmission.setScore(score);
        quizSubmission.setStartTime(req.getStartTime());
        quizSubmission.setFinishTime(req.getFinishTime());
        quizSubmissionService.save(quizSubmission);

        resp.put("score", score);
        resp.put("submitQuestionsCount", submitQuestionsCount);
        resp.put("startTime", req.getStartTime());
        resp.put("finishTime", req.getFinishTime());
        resp.put("numOfQuestions", quiz.getQuestions().size());
        resp.put("totalScore", quizService.getTotalScore(quiz.getQuestions()));
        resp.put("answers", answers);

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
        List<QuizSubmission> submissions = quizSubmissionService.getSubmissions(user);
        List<QuizSubmissionResponse> joinedQuizzes = submissions.stream()
                .map(QuizMapper::toQuizSubmissionResponse)
                .collect(Collectors.toList());
        rs.put("joinedQuizzes", joinedQuizzes);
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
        List<Assignment> assignmentQuizzes = assignmentService.getAssignmentQuizzes(
                currentUser);
        List<AssignmentQuizResponse> rs = assignmentQuizzes.stream().map(a -> {
                    Quiz quiz = a.getQuiz();
                    int totalUser = assignmentService.countAssignedUsers(quiz);
                    int completed = quizSubmissionService.countUserSubmittedQuiz(a.getUser(), quiz);
                    AssignmentQuizResponse assignmentQuizResponse = new AssignmentQuizResponse(
                            a.getStartDate(), a.getFinishDate(), a.getCreatedDate(),
                            quiz.getId(), quiz.getTitle(), quiz.getQuizImage(), totalUser, completed
                    );
                    return assignmentQuizResponse;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.successData(rs));
    }

    /**
     * get assigned quiz users
     *
     * @param quizId
     * @return
     */
    public ResponseEntity<?> getAssignedUsers(Long quizId) {
        Quiz quiz = quizService.getCurrentUserQuiz(quizId);
        if (quiz == null) {
            return ResponseEntity.badRequest().body(BaseResponse.badRequest("Quiz with id: " + quizId + " is not exist!"));
        }
        QuizOverview quizOverview = QuizMapper.toQuizOverview(quiz, quizService.getPlayedCount(quiz));
        Map<String, Object> rs = new HashMap<>();
        rs.put("quiz", quizOverview);
        List<Assignment> assignments = assignmentService.getAssignment(quiz);
        SimpleDateFormat dft = new SimpleDateFormat("HH:mm dd-MM");

        List<Object> users = assignments.stream().map(a -> {
                    User user = a.getUser();
                    Map<String, Object> mapUser = new HashMap<>();
                    mapUser.put("email", user.getEmail());
                    mapUser.put("username", user.getUsername());
                    mapUser.put("fullName", user.getFullName());
                    mapUser.put("avt", user.getAvtImgUrl());
                    mapUser.put("assignDate", dft.format(a.getCreatedDate()));
                    List<QuizSubmission> userSubmissionQuiz = quizSubmissionService.getUserSubmissionQuiz(user, quizId);
                    if (userSubmissionQuiz != null) {
                        List<HashMap<Object, Object>> submissions = userSubmissionQuiz.stream().map(quizSubmission -> {
                            HashMap<Object, Object> map = new HashMap<>();
                            Double score = quizSubmission.getScore();
                            map.put("score", score);
                            Date completeDate = quizSubmission.getFinishTime();
                            map.put("finishTime", dft.format(completeDate));
                            return map;
                        }).collect(Collectors.toList());
                        mapUser.put("complete", true);
                        mapUser.put("submissions", submissions);
                    } else {
                        mapUser.put("complete", false);
                    }
                    return mapUser;
                })
                .collect(Collectors.toList());
        rs.put("users", users);
        return ResponseEntity.ok(BaseResponse.successData(rs));

    }

    /**
     * get assigned quizzes
     *
     * @return
     */
    public ResponseEntity<?> getAssignedQuizzes() {
        List<Assignment> assignedQuizzes =
                userService.getAssignedQuizzes();
        User user = userService.getCurrentUser();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM");
        List<Object> rs = assignedQuizzes.stream()
                .map(
                        a -> {

                            Quiz quiz = a.getQuiz();
                            Map<String, Object> map = new HashMap<>();
                            map.put("quiz", QuizMapper.toQuizOverview(quiz, quizService.getPlayedCount(quiz)));
                            Map<String, Object> mapUser = new HashMap<>();
                            mapUser.put("email", user.getEmail());
                            mapUser.put("username", user.getUsername());
                            mapUser.put("fullName", user.getFullName());
                            mapUser.put("avt", user.getAvtImgUrl());
                            mapUser.put("assignDate", sdf.format(a.getCreatedDate()));
//                            QuizSubmission userSubmissionQuiz = quizSubmissionService.getUserSubmissionQuiz(user, quiz.getId());
//                            if (userSubmissionQuiz != null) {
//                                Double score = userSubmissionQuiz.getScore();
//                                mapUser.put("complete", true);
//                                mapUser.put("score", score);
//                                mapUser.put("complete", 1);
//                                Date completeDate = userSubmissionQuiz.getCreateDate();
//                                mapUser.put("completeDate", sdf.format(completeDate));
//
//                            } else {
//                                mapUser.put("complete", false);
//                            }
                            return map;
                        }
                )
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.successData(rs, "Get assigned quizzes success!"));

    }

    /**
     * assign to users
     *
     * @param assignmentReq
     * @return
     */
    public ResponseEntity<?> assignToUsers(AssignmentRequest assignmentReq) {
        Set<String> emails = assignmentReq.getEmails();
        Date startDate = assignmentReq.getStartDate();
        Date finishDate = assignmentReq.getFinishDate();
        if (emails != null && !emails.isEmpty()) {
            Long quizId = assignmentReq.getQuizId();
            Quiz quiz = quizService.getByIdAndUser(quizId, quizService.getCurrentUser());
            if (quiz == null) {
                return ResponseEntity.badRequest().body(BaseResponse.badRequest("Quiz with id: " + quizId + " is not exist!"));
            }
            for (String email :
                    emails) {
                User user = userService.getByEmail(email);
                if (user != null) {
                    Assignment assignment = assignmentService.getByUserAndQuiz(user, quiz);

                    String fullName = user.getFullName();
                    String username = user.getUsername();
                    try {
                        if (assignment != null) {
                            mailService.sendAssignUpdateToUser(username == null ? fullName : username, email, quiz.getTitle(), quiz.getCode(), startDate, finishDate, new Date());
                        } else {
                            Assignment.QuizAssignmentId id = new Assignment.QuizAssignmentId(user.getUid(), quiz.getId());
                            assignment = new Assignment();
                            assignment.setId(id);
                            user.getAssignedQuizzes().add(assignment);
                            mailService.sendAssignQuizToUser(username == null ? fullName : username, email, quiz.getTitle(), quiz.getCode(), startDate, finishDate);
                        }
                        assignment.setStartDate(startDate);
                        assignment.setFinishDate(finishDate);
                        assignment.setUser(user);
                        assignment.setQuiz(quiz);
                        assignment.setActive(1);
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
}
