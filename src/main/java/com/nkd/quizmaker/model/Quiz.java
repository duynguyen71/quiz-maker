package com.nkd.quizmaker.model;

import com.nkd.quizmaker.enumm.EQuizStatus;
import com.nkd.quizmaker.enumm.EQuizType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

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
@ToString
@EqualsAndHashCode
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true, updatable = false, nullable = false)
    private String code;

    private String quizImage;

    private Date startDate;

    private Date finishDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private EQuizType quizType;


    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Column(columnDefinition = "tinyint(2) default 1")
    private Integer status;

    private Integer limitTime;

    @Column(columnDefinition = "tinyint(2) default 0")
    private int visibility;

    @OneToMany(
            mappedBy = "quiz",
            targetEntity = Assignment.class,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Assignment> users = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "user_quiz",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> owners = new ArrayList<>();


    @OneToMany(targetEntity = QuizSubmission.class, mappedBy = "quiz")
    private List<QuizSubmission> quizSubmissions = new ArrayList<>();


    @ManyToMany(mappedBy = "quizzes", fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    @Where(clause = "active=1")
    private List<Question> questions = new ArrayList<>();

    @Column
    private Integer active = 1;

    public void addQuestion(Question question) {
        this.questions.add(question);
        question.getQuizzes().add(this);
    }


}
