package com.nkd.quizmaker.repo;

import com.nkd.quizmaker.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    boolean existsById(long optionId);
}
