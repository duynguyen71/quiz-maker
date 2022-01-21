package com.nkd.quizmaker.helper;

import com.nkd.quizmaker.model.Streak;
import com.nkd.quizmaker.response.BaseResponse;
import com.nkd.quizmaker.service.StreakService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("StreakHelper")
@AllArgsConstructor
public class StreakHelper {

    private final StreakService streakService;
    private final UserService userService;

    public ResponseEntity<?> getStreaks() {
        List<Streak> streaks = streakService.getStreaksByUser(userService.getCurrentUser());
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy HH:mm");
        List<Map<String, Object>> rs = streaks.stream().map(streak -> {
            Map<String, Object> map = new HashMap<>();
            map.put("loginDate", sdf.format(streak.getLoginDate()));
            map.put("lastLoginDate", sdf.format(streak.getLastLoginDate()));
            map.put("count", streak.getCount());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.successData(rs));
    }
}
