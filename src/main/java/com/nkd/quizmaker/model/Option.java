package com.nkd.quizmaker.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "options")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    private Double score;

    @ManyToMany
    @JoinTable(name = "option_question",
            joinColumns = @JoinColumn(name = "option_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private List<Question> questions = new ArrayList<>();


    private String tExplanation;

    private String fExplanation;

    @OneToMany(mappedBy = "option")
    private List<SubmissionAnswer> submissionAnswers = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Option)) return false;
        Option option = (Option) o;
        return Objects.equals(getId(), option.getId()) && Objects.equals(getContent(), option.getContent()) && Objects.equals(getScore(), option.getScore()) && Objects.equals(getQuestions(), option.getQuestions()) && Objects.equals(tExplanation, option.tExplanation) && Objects.equals(fExplanation, option.fExplanation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getContent(), getScore(), getQuestions(), tExplanation, fExplanation);
    }


}
