package com.nkd.quizmaker.mapper;

import com.nkd.quizmaker.model.Assignment;
import com.nkd.quizmaker.model.AssignmentInfo;
import com.nkd.quizmaker.response.AssignedQuizResponse;
import com.nkd.quizmaker.response.QuizOverview;

import java.util.HashMap;
import java.util.Map;

public class AssignmentMapper {

    public static Map<String, Object> toAssignmentInfoResponse(AssignmentInfo assignmentInfo, int playedCount) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", assignmentInfo.getId());
        map.put("title", assignmentInfo.getTitle());
        map.put("description", assignmentInfo.getDescription());
        map.put("startDate", assignmentInfo.getStartDate());
        map.put("finishDate", assignmentInfo.getFinishDate());
        map.put("status", assignmentInfo.getStatus());
        map.put("quiz", QuizMapper.toQuizOverview(assignmentInfo.getQuiz(), playedCount));
        return map;
    }

    public static Map<String, Object> toAssignmentResponse(Assignment assignment) {
        Map<String, Object> map = new HashMap<>();
        AssignmentInfo assignmentInfo = assignment.getAssignmentInfo();
        map.put("id", assignmentInfo.getId());
        map.put("title", assignmentInfo.getTitle());
        map.put("description", assignmentInfo.getDescription());
        map.put("startDate", assignmentInfo.getStartDate());
        map.put("finishDate", assignmentInfo.getFinishDate());
        map.put("createDate", assignment.getCreatedDate());
        map.put("status", assignment.getStatus());
        map.put("quiz", QuizMapper.toQuizOverview(assignmentInfo.getQuiz(), null));
        return map;
    }


}
