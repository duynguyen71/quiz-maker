package com.nkd.quizmaker.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @EmbeddedId
    private QuizAssignmentId id;

    @ManyToOne
    private AssignmentInfo assignmentInfo;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date", updatable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date")
    private Date updateDate;

    private int status = 1;

    private int active = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("quizId")
    private Quiz quiz;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class QuizAssignmentId implements Serializable {

        @Column(name = "user_id")
        private Long userId;

        @Column(name = "quiz_id")
        private Long quizId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QuizAssignmentId)) return false;
            QuizAssignmentId that = (QuizAssignmentId) o;
            return Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getQuizId(), that.getQuizId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getUserId(), getQuizId());
        }
    }
}

