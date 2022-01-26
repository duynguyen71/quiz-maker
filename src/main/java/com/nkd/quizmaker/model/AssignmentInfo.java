package com.nkd.quizmaker.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssignmentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "HH:mm dd-MM-yyyy")
    private Date startDate;

    private String title;

    private String description;

    @JsonFormat(pattern = "HH:mm dd-MM-yyyy")
    private Date finishDate;

    private Integer status = 1;

    private Integer active = 1;

    @OneToMany(mappedBy = "assignmentInfo")
    private List<Assignment> assignment;

    @ManyToOne
    private Quiz quiz;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
}
