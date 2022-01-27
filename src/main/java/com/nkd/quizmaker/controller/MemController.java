package com.nkd.quizmaker.controller;

import com.nkd.quizmaker.helper.QuestionHelper;
import com.nkd.quizmaker.helper.QuizHelper;
import com.nkd.quizmaker.helper.StreakHelper;
import com.nkd.quizmaker.helper.UserHelper;
import com.nkd.quizmaker.request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemController {

    private final UserHelper userHelper;
    private final QuizHelper quizHelper;
    private final QuestionHelper questionHelper;
    private final StreakHelper streakHelper;

    @GetMapping("/users/searching")
    public ResponseEntity<?> searchUsers(@RequestParam Map<String, String> params) {
        return userHelper.searchUsers(params);
    }

    @GetMapping("/users/me")
    public ResponseEntity<?> getCurrentUserDetail() {
        return userHelper.getCurrentUserDetail();
    }

    @GetMapping("/users/me/streaks")
    public ResponseEntity<?> getStreaks() {
        return streakHelper.getStreaks();
    }


    @PostMapping("/users/me/avt")
    public ResponseEntity<?> updateAvatar(@RequestParam("file") MultipartFile file) {
        return userHelper.updateAvt(file);
    }

    @PostMapping("/users/me")
    public ResponseEntity<?> updateUserDetail(@RequestBody UpdateUserRequest req) {
        return userHelper.updateUserDetail(req);
    }

    @GetMapping("/users/me/quizzes")
    public ResponseEntity<?> getUserQuizzes(@RequestParam Map<String, String> params) {
        return userHelper.getUserQuizzes(params);
    }

    @PostMapping("/users/me/quizzes")
    public ResponseEntity<?> saveQuiz(@RequestBody @Valid QuizRequest quizRequest) {
        return quizHelper.save(quizRequest);
    }

    @GetMapping("/users/me/quizzes/{id}")
    public ResponseEntity<?> getUserQuiz(@PathVariable("id") Long id) {
        return userHelper.getUserQuiz(id);
    }

    @PostMapping("/users/me/quizzes/{id}")
    public ResponseEntity<?> removeQuiz(@PathVariable("id") Long id, @RequestParam("active") Integer active) {
        return userHelper.removeQuiz(id, active);
    }

    @DeleteMapping("/user/me/questions/{id}")
    public ResponseEntity<?> removeQuestion(@PathVariable("id") Long questionId) {
        return quizHelper.removeQuestion(questionId);
    }

    @PostMapping("/users/me/submission-answers")
    public ResponseEntity<?> submissionAnswers(@RequestBody @Valid QuizSubmissionRequest req) {
        return userHelper.submitFunQuiz(req);
    }

    @PostMapping("/users/me/exam/submission-answers")
    public ResponseEntity<?> submissionExamQuiz(@RequestBody @Valid QuizSubmissionRequest req) {
        return userHelper.submitExamQuiz(req);
    }

    @GetMapping("/users/{userId}/exam/submission-answers/{id}")
    public ResponseEntity<?> getExamSubmitAnswers(@PathVariable("id") Long quizId, @PathVariable("userId") Long uid) {
        return userHelper.getExamSubmitAnswers(quizId, uid);
    }

    @GetMapping("/users/me/submission-answers")
    public ResponseEntity<?> getSubmittedQuizzes() {
        return userHelper.getSubmittedQuizzes();
    }


    @GetMapping("/users/me/assignments/info")
    public ResponseEntity<?> getAssignmentDetail() {
        return userHelper.getAssigmentQuizzes();
    }

    @PostMapping("/users/me/assigment/assign-users")
    public ResponseEntity<?> assignQuizToUsers(@Valid @RequestBody AssignmentRequest request) {
        return userHelper.assignToUsers(request);
    }

    @GetMapping("/users/me/assignment/quizzes/{id}/users")
    public ResponseEntity<?> getAssignedUsers(@PathVariable("id") Long assignInfoId) {
        return userHelper.getAssignedUsers(assignInfoId);
    }


    @GetMapping("/quizzes/questions")
    public ResponseEntity<?> getQuestions(@RequestParam Map<String, String> params) {
        return questionHelper.getQuestions(params);
    }

    @GetMapping("/users/me/assigned-quizzes")
    public ResponseEntity<?> getAssignedQuizzes() {
        return userHelper.getAssignedQuizzes();
    }

    @GetMapping("/users/me/assigned-quizzes/{id}")
    public ResponseEntity<?> getAssignedQuiz(@PathVariable("id") Long quizId) {
        return userHelper.getAssignedQuiz(quizId);
    }

    @GetMapping("/users/me/assigned-quizzes/{code}/detail")
    public ResponseEntity<?> getAssignedQuizDetail(@PathVariable("code") String code) {
        return userHelper.getAssignedQuizDetail(code);
    }

    @GetMapping("/assigment/quizzes/{quizId}/users")
    public ResponseEntity<?> getAssignmentReport(@PathVariable("quizId") Long aInfoId) {
        return userHelper.getAssignmentReport(aInfoId);
    }

    @GetMapping("/users/me/submission/quizzes/{code}")
    public ResponseEntity<?> getSubmissionQuizAnswer(@PathVariable("code") String code) {
        return userHelper.getSubmissionAnswer(code);
    }


    @GetMapping("/users/me/change-password")
    public ResponseEntity<?> changePasswordRequest() {
        return userHelper.changePasswordRequest();
    }

    @PostMapping("/users/me/change-password")
    public ResponseEntity<?> changePasswordRequest(
            @RequestBody @Valid ChangePasswordRequest req) {
        return userHelper.changePassword(req);
    }

}
