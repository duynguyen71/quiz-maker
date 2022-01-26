package com.nkd.quizmaker.model;

import com.nkd.quizmaker.enumm.ESubmitType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class QuizSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Date startTime;

    private Date finishTime;

    private Double score;

    private int completeCount;

    private Integer attempt = 0;

    @Column
    private Integer status = 0;

    @OneToMany(mappedBy = "quizSubmission")
    private List<SubmissionAnswer> answers = new ArrayList<>();

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date", updatable = false)
    private Date createDate;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ESubmitType submissionType;

}
