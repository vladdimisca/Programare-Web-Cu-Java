package com.web.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identity_card")
    private String identityCard;

    @Column(name = "medical_certificate")
    private String medicalCertificate;

    @Column(name = "diploma")
    private String diploma;

    @OneToOne(mappedBy = "document")
    private User user;
}
