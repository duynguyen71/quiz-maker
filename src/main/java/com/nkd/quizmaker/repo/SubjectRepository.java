package com.nkd.quizmaker.repo;

import com.nkd.quizmaker.model.Subject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    boolean existsById(long subjectId);

    boolean existsByTitle(String name);

    Subject getByTitle(String name);

    Optional<Subject> findByTitle(String name);

    @Query(nativeQuery = true,
    value = "SELECT * FROM subject s WHERE (:title IS NULL OR s.title LIKE :title)"
    )
    List<Subject> getSubjects(String title,Pageable pageable);
}
