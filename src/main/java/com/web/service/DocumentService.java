package com.web.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.web.error.ErrorMessage;
import com.web.error.exception.BadRequestException;
import com.web.error.exception.ConflictException;
import com.web.error.exception.InternalServerErrorException;
import com.web.error.exception.NotFoundException;
import com.web.model.Document;
import com.web.model.User;
import com.web.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final AmazonS3 s3Client;
    private final UserService userService;
    private final SecurityService securityService;

    @Autowired
    public DocumentService(DocumentRepository documentRepository,
                           AmazonS3 s3Client,
                           UserService userService,
                           SecurityService securityService) {
        this.documentRepository = documentRepository;
        this.s3Client = s3Client;
        this.userService = userService;
        this.securityService = securityService;
    }

    @Transactional
    public Document create(MultipartFile identityCard, MultipartFile medicalCertificate, MultipartFile diploma) {
        UUID userId = securityService.getUserId();
        User user = userService.getById(userId);
        if (user.getDocument() != null) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "Documents");
        }

        Document document = new Document();
        user.setDocument(document);
        document.setUser(user);

        checkContentType(identityCard, "identityCard");
        checkContentType(medicalCertificate, "medicalCertificate");
        checkContentType(diploma, "diploma");

        s3Client.createBucket(userId.toString());

        try {
            document.setIdentityCard(uploadFile(userId.toString(), identityCard, "identityCard"));
            document.setMedicalCertificate(uploadFile(userId.toString(), medicalCertificate, "medicalCertificate"));
            document.setDiploma(uploadFile(userId.toString(), diploma, "diploma"));
        } catch (IOException e) {
            e.printStackTrace();
            s3Client.deleteBucket(userId.toString());
            throw new InternalServerErrorException(ErrorMessage.INTERNAL_SERVER_ERROR);
        }

        return documentRepository.save(document);
    }

    @Transactional
    public Document update(Long id, MultipartFile identityCard, MultipartFile medicalCertificate, MultipartFile diploma) {
        Document document = getById(id);
        UUID userId = document.getUser().getId();

        checkNotSubmitted(document);
        checkContentType(identityCard, "identityCard");
        checkContentType(medicalCertificate, "medicalCertificate");
        checkContentType(diploma, "diploma");

        try {
            document.setIdentityCard(uploadFile(userId.toString(), identityCard, "identityCard"));
            document.setMedicalCertificate(uploadFile(userId.toString(), medicalCertificate, "medicalCertificate"));
            document.setDiploma(uploadFile(userId.toString(), diploma, "diploma"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(ErrorMessage.INTERNAL_SERVER_ERROR);
        }

        return documentRepository.save(document);
    }

    public Document getById(Long id) {
        Document document = documentRepository.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorMessage.NOT_FOUND, "document", id));

        securityService.authorize(document.getUser().getId(),  "document", id);
        return document;
    }

    public List<Document> getAll() {
        return documentRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        Document document = getById(id);
        checkNotSubmitted(document);

        document.getUser().setDocument(null);
        documentRepository.delete(document);
    }

    private void checkContentType(MultipartFile file, String name) {
        List<String> allowedContentTypes = List.of("application/pdf", "image/.*");

        if (allowedContentTypes.stream().noneMatch((type) -> Objects.requireNonNull(file.getContentType()).matches(type))) {
            throw new BadRequestException(ErrorMessage.INVALID_CONTENT_TYPE, name, allowedContentTypes);
        }
    }

    private String uploadFile(String bucketName, MultipartFile multipartFile, String objectName) throws IOException {
        File file = File.createTempFile("temp", "." + getFileExtension(multipartFile.getContentType()));
        multipartFile.transferTo(file);
        PutObjectResult result = s3Client.putObject(bucketName, objectName, file);
        return result.getMetadata().getRawMetadata().get("Location") + objectName;
    }

    private String getFileExtension(String contentType) {
        return contentType != null ? contentType.split("/")[1] : null;
    }

    private void checkNotSubmitted(Document document) {
        if (document.getUser().getAdmissionFile() != null) {
            throw new ConflictException(ErrorMessage.ADMISSION_FILE_ALREADY_SUBMITTED);
        }
    }
}
