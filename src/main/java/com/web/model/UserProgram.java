package com.web.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users_programs")
public class UserProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_program", referencedColumnName = "id")
    private ProgramOfStudy programOfStudy;

    @Column(name = "grade")
    private Integer grade;
}
