package com.nkd.quizmaker.repo;

import com.nkd.quizmaker.model.Streak;
import com.nkd.quizmaker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface StreakRepository extends JpaRepository<Streak, Long> {

    List<Streak> findByUser(User user);

    int countDistinctByUser(User user);

    Optional<Streak> findDistinctTopByUserOrderByLoginDateDesc(User user);

    List<Streak> findDistinctTop2ByUserOrderByLoginDateDesc(User user);

    List<Streak> findDistinctByUserAndLoginDateBetween(User user, Date toDay, Date fiveDaysBefore);

    List<Streak> findDistinctTop5ByUserOrderByLoginDateDesc(User user);

    List<Streak> findByUserAndLoginDateLessThanEqualAndLoginDateGreaterThanEqual(User user, Date endDate,Date startDate);

}
