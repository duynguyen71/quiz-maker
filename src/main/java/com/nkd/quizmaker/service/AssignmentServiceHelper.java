package com.nkd.quizmaker.service;

import com.nkd.quizmaker.service.MailSenderHelper;
import com.nkd.quizmaker.model.*;
import com.nkd.quizmaker.repo.*;
import com.nkd.quizmaker.request.AssignmentRequest;
import com.nkd.quizmaker.response.AssignmentResponse;
import com.nkd.quizmaker.response.QuizSubmitResponseDetail;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Component("AssignmentServiceHelper")
public class AssignmentServiceHelper {

    private final AssignmentRepository assignRepo;
    private final UserRepository userRepo;
    private final QuizRepository quizRepo;
    private final QuizSubmissionRepository quizSubmissionRepo;
    private final SubmissionAnswerRepository submissionAnswerRepo;
    private final MailSenderHelper mailSenderHelper;

    /*

     */
    public ResponseEntity<?> getAssignedUsersByQuiz(long quizId) {
        Quiz quiz = quizRepo.getById(quizId);
        List<Assignment> assignments = assignRepo.findByQuiz(quiz);
        List<Map<String, Object>> rs = assignments.stream().map(assignment -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", assignment.getId().getUserId());
            map.put("quizId", assignment.getId().getQuizId());
            map.put("email", assignment.getUser().getEmail());
            map.put("username", assignment.getUser().getUsername());

            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(rs);
    }

    /**
     * @param userId
     * @param quizId
     * @return
     */
    public ResponseEntity<?> removeAssignedUser(long userId, long quizId) {
        Assignment.QuizAssignmentId id = new Assignment.QuizAssignmentId(userId, quizId);
        boolean existsById = assignRepo.existsById(id);
        int i = quizRepo.existsByUserIdAndQuizId(getCurrentUid(), quizId);
        if (i == 1 && existsById) {
            assignRepo.deleteById(id);
            return ResponseEntity.ok("Remove assigned user success");
        }
        return ResponseEntity.ok("Failed to remove");
    }

    /**
     * assign quiz to students
     *
     * @param request
     * @return
     */
    public ResponseEntity<?> assignToStudents(AssignmentRequest request) {
        //TODO:CHECK QUiZ BELONG TO ADMIN
        Optional<Quiz> optionalQuiz = quizRepo.findById(request.getQuizId());
        if (optionalQuiz.isEmpty()) {
            return ResponseEntity.badRequest().body("Quiz is not exist");
        }
        Quiz quiz = optionalQuiz.get();
        Set<String> emails = request.getEmails();
        if (emails != null) {
            for (String email :
                    emails) {
                Optional<User> optionalUser = userRepo.findByEmail(email);
                if (optionalUser.isPresent()) {
                    Assignment assignment = new Assignment();
                    User user = optionalUser.get();
                    assignment.setId(new Assignment.QuizAssignmentId(user.getUid(), quiz.getId()));
                    assignment.setStatus(0);
                    assignment.setActive(1);
                    assignment.setStartDate(request.getStartDate());
                    assignment.setFinishDate(request.getFinishDate());
                    assignment.setUser(user);
                    assignment.setQuiz(quiz);
                    assignRepo.save(assignment);
                    //send mail if email existed
//                    mailSenderHelper.sendAssignedQuizMessage(email, "Thu moi tham gia quiz", "Moi ban tham gia quiz 89832");
                }
            }
            return ResponseEntity.ok("Add all");
        }
        return ResponseEntity.ok("Success");
    }

    public ResponseEntity<?> findAllByUser(long userId) {
        List<Assignment> list = assignRepo.findByUser(userRepo.getById(userId));
        List<AssignmentResponse> rs = list.stream().map(assignment -> {
            AssignmentResponse resp = new AssignmentResponse();
            resp.setQuizId(assignment.getQuiz().getId());
            resp.setCode(assignment.getQuiz().getCode());
            resp.setCreatedDate(assignment.getCreatedDate());
            return resp;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(rs);
    }

    public ResponseEntity<?> getQuizzesSubmitReports() {
        List<Quiz> quizzes = quizRepo.findByUser(getCurrentUid(),null,null,null);
        List<Object> rs = new ArrayList<>();
        for (Quiz quiz :
                quizzes) {
            Map<String, Object> quizReport = new HashMap<>();
            quizReport.put("title", quiz.getTitle());
            quizReport.put("quizId", quiz.getId());
            //find by quiz and status
            //TODO: sua cho nay
            List<QuizSubmission> submissions =
                    quizSubmissionRepo.findByQuiz(quiz);

            List<Object> participants = new ArrayList<>();
            for (QuizSubmission quizSubmission :
                    submissions) {
                quizReport.put("id", quizSubmission.getId());
                ParticipantReport participantReport = new ParticipantReport();
                participantReport.setUsername(quizSubmission.getUser().getUsername());
                participantReport.setEmail(quizSubmission.getUser().getEmail());
                participantReport.setScore(quizSubmission.getScore() != null ? quizSubmission.getScore() : 0);
                participantReport.setSubmittedTime(quizSubmission.getCreateDate());
                participants.add(participantReport);
            }
            quizReport.put("participants", participants);

            rs.add(quizReport);
        }
        return ResponseEntity.ok(rs);
    }

    public ResponseEntity<?> getQuizSubmitReportDetails(long quizID) {
        Optional<Quiz> optionalQuiz = quizRepo.findById(quizID);
        if (optionalQuiz.isEmpty())
            return ResponseEntity.badRequest().body("Quiz with id: " + quizID + " does not exist");
        List<QuizSubmission> quizSubmissions = quizSubmissionRepo.findByQuiz(optionalQuiz.get());
        List<QuizSubmitResponseDetail> rs = quizSubmissions.stream().map(quizSubmission -> new QuizSubmitResponseDetail(quizSubmission)).collect(Collectors.toList());
        return ResponseEntity.ok(
                rs
        );
    }

    @Data
    static class ReportDetail {
        private String username;
        private String email;
        private List<Question> questions;
        private Date createDate;

    }

    @Data
    static class ParticipantReport {
        String username;
        String email;
        Double score;
        Date submittedTime;
    }

    long getCurrentUid() {
        return ((MyUserDetails) (SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()))
                .getUser()
                .getUid();
    }
}
