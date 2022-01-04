package com.web.model;

import com.web.model.enumeration.CivilStatus;
import com.web.model.enumeration.SexType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "user_infos")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "cnp")
    private String cnp;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "civil_status")
    @Enumerated(EnumType.STRING)
    private CivilStatus civilStatus;

    @Column(name = "sex")
    @Enumerated(EnumType.STRING)
    private SexType sex;

    @OneToOne(mappedBy = "userInfo", fetch = FetchType.LAZY)
    private User user;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_address", referencedColumnName = "id")
    private Address address;
}
