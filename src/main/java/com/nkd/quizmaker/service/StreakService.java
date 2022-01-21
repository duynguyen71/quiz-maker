package com.nkd.quizmaker.service;

import com.nkd.quizmaker.model.Streak;
import com.nkd.quizmaker.model.User;
import com.nkd.quizmaker.repo.StreakRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreakService {

    private final StreakRepository streakRepo;

    public List<Streak> getStreaksByUser(User user) {
//        List<Streak> streaks = streakRepo.findDistinctByUserAndLoginDateBetween(user, getDateWithoutTime(new Date()), getDateWithoutTime(subtractDays(new Date(), 5)));
        List<Streak> streaks = streakRepo.findByUserAndLoginDateLessThanEqualAndLoginDateGreaterThanEqual(user, new Date(),
                subtractDays(new Date(), 5)
        );
        int size = streaks.size();


        for (Streak s :
                streaks) {
            log.info("LOGIN DATE - {} ", s.getLoginDate().toString());
        }
        return streakRepo.findByUser(user);
    }

    public Date getDateWithoutTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Streak getStreakByDate(Date date) {
        return null;
    }

    public Streak getTopStreak(User user) {
        Optional<Streak> optional
                = streakRepo.findDistinctTopByUserOrderByLoginDateDesc(user);
        return optional.orElse(null);
    }

    public Streak save(User user) {
        log.info("update streak");
        Streak streak = getTopStreak(user);
        Streak top2Streak = getTop2Streak(user);
        if (streak == null) {
            streak = new Streak();
            streak.setUser(user);
            streak.setCount(1);
            return streakRepo.save(streak);
        }

        if (streak != null && !equalDayMonthYear(streak.getLoginDate(), new Date())) {
            //cach nhau 1 ngay
            if (equalDayMonthYear(addDays(streak.getLoginDate(), 1), new Date())) {
                Streak s = new Streak();
                s.setUser(user);
                int i = streak.getCount() + 1;
                s.setCount(i);
                streakRepo.save(s);
            } else {
                Streak s = new Streak();
                s.setUser(user);
                s.setCount(1);
                streakRepo.save(s);
            }
        }
        return null;

    }

    public Streak getTop2Streak(User user) {
        List<Streak> optional = streakRepo.findDistinctTop2ByUserOrderByLoginDateDesc(user);
        if (!optional.isEmpty() && optional.size() == 2) {
            return optional.get(1);
        }
        return null;
    }

    public boolean equalDayMonthYear(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
                && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    public Date addDays(Date date, Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    public Date subtractDays(Date date, Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime();
    }

    public Calendar toCalendar(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }
}
