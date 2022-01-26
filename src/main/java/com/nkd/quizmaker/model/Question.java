package com.nkd.quizmaker.model;

import com.nkd.quizmaker.enumm.EAnswerType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private EAnswerType optionType;

    @Column(name = "position")
    private Integer position;


    @ManyToMany(mappedBy = "questions")
    private List<Option> options = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "question_quiz",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    private List<Quiz> quizzes = new ArrayList<>();

    @OneToMany(mappedBy = "question")
    private List<SubmissionAnswer> submissionAnswers = new ArrayList<>();


    @Temporal(
            TemporalType.TIMESTAMP
    )
    @CreationTimestamp
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modifyDate;

    private Integer active = 1;


}
