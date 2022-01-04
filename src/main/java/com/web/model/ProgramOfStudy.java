package com.web.model;

import com.web.model.enumeration.FinancingType;
import com.web.model.enumeration.ProgramType;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "programs_of_study")
public class ProgramOfStudy {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ProgramType type;

    @Column(name = "number_of_years")
    private Integer numberOfYears;

    @Column(name = "number_of_students")
    private Integer numberOfStudents;

    @Column(name = "financing_type")
    @Enumerated(EnumType.STRING)
    private FinancingType financingType;

    @OneToMany(mappedBy = "programOfStudy", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserProgram> userPrograms;
}
