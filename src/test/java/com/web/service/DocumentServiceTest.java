package com.web.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.web.error.exception.BadRequestException;
import com.web.error.exception.ConflictException;
import com.web.error.exception.InternalServerErrorException;
import com.web.error.exception.NotFoundException;
import com.web.model.AdmissionFile;
import com.web.model.Document;
import com.web.model.User;
import com.web.model.enumeration.UserRole;
import com.web.repository.DocumentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    private static final Long ID = 1L;
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private UserService userService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private DocumentService documentService;

    @Test
    @DisplayName("Create document - success")
    void create_success() {
        User user = getUser();
        PutObjectResult result = getS3Result();
        Document document = getDocument();

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);
        when(s3Client.putObject(any(), any(), any(File.class))).thenReturn(result);
        when(documentRepository.save(any())).thenReturn(document);

        MultipartFile identityCard = getMultipartFile(true);
        MultipartFile medicalCertificate = getMultipartFile(true);
        MultipartFile diploma = getMultipartFile(true);

        Document resultedDocument = documentService.create(identityCard, medicalCertificate, diploma);

        assertNotNull(resultedDocument);
        assertEquals(document.getId(), resultedDocument.getId());
        assertEquals(document.getDiploma(), resultedDocument.getDiploma());
        assertEquals(document.getIdentityCard(), resultedDocument.getIdentityCard());
    }

    @Test
    @DisplayName("Create document - already exists - failure")
    void create_alreadyExists_failure() {
        User user = getUser();
        user.setDocument(getDocument());

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);

        MultipartFile identityCard = getMultipartFile(false);
        MultipartFile medicalCertificate = getMultipartFile(false);
        MultipartFile diploma = getMultipartFile(false);

        assertThrows(ConflictException.class, () -> documentService.create(identityCard, medicalCertificate, diploma));
    }

    @Test
    @DisplayName("Create document - IOException - failure")
    void create_IOException_failure() throws IOException {
        User user = getUser();

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);

        MultipartFile identityCard = getMultipartFile(true);
        MultipartFile medicalCertificate = getMultipartFile(true);
        MultipartFile diploma = getMultipartFile(true);

        doThrow(new IOException()).when(identityCard).transferTo(any(File.class));

        assertThrows(InternalServerErrorException.class, () ->
                documentService.create(identityCard, medicalCertificate, diploma));
    }

    @Test
    @DisplayName("Create document - invalid content type - failure")
    void create_invalidContentType_failure() {
        User user = getUser();

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);

        MultipartFile identityCard = getMultipartFile(true);
        when(identityCard.getContentType()).thenReturn("dummy");
        MultipartFile medicalCertificate = getMultipartFile(false);
        MultipartFile diploma = getMultipartFile(false);

        assertThrows(BadRequestException.class, () -> documentService.create(identityCard, medicalCertificate, diploma));
    }

    @Test
    @DisplayName("Update document - success")
    void update_success() {
        User user = getUser();
        PutObjectResult result = getS3Result();
        Document document = getDocument();
        document.setUser(user);

        when(s3Client.putObject(any(), any(), any(File.class))).thenReturn(result);
        when(documentRepository.findById(ID)).thenReturn(Optional.of(document));
        when(documentRepository.save(any())).thenReturn(document);

        MultipartFile identityCard = getMultipartFile(true);
        MultipartFile medicalCertificate = getMultipartFile(true);
        MultipartFile diploma = getMultipartFile(true);

        Document resultedDocument = documentService.update(ID, identityCard, medicalCertificate, diploma);

        assertNotNull(resultedDocument);
        assertEquals(document.getId(), resultedDocument.getId());
        assertEquals(document.getDiploma(), resultedDocument.getDiploma());
        assertEquals(document.getIdentityCard(), resultedDocument.getIdentityCard());
    }

    @Test
    @DisplayName("Update document - IOException - failure")
    void update_IOException_failure() throws IOException {
        User user = getUser();
        Document document = getDocument();
        document.setUser(user);

        when(documentRepository.findById(ID)).thenReturn(Optional.of(document));

        MultipartFile identityCard = getMultipartFile(true);
        MultipartFile medicalCertificate = getMultipartFile(true);
        MultipartFile diploma = getMultipartFile(true);

        doThrow(new IOException()).when(identityCard).transferTo(any(File.class));

        assertThrows(InternalServerErrorException.class, () ->
                documentService.update(ID, identityCard, medicalCertificate, diploma));
    }

    @Test
    @DisplayName("Update document - submitted admission file - failure")
    void update_submittedAdmissionFile_failure() {
        User user = getUser();
        user.setAdmissionFile(new AdmissionFile());
        Document document = getDocument();
        document.setUser(user);

        when(documentRepository.findById(ID)).thenReturn(Optional.of(document));

        MultipartFile identityCard = getMultipartFile(false);
        MultipartFile medicalCertificate = getMultipartFile(false);
        MultipartFile diploma = getMultipartFile(false);

        assertThrows(ConflictException.class, () -> documentService.update(ID, identityCard, medicalCertificate, diploma));
    }

    @Test
    @DisplayName("Get document - success")
    void getById_success() {
        Document document = getDocument();
        document.setUser(getUser());

        when(documentRepository.findById(ID)).thenReturn(Optional.of(document));

        Document resultedDocument = documentService.getById(ID);

        assertNotNull(resultedDocument);
        assertEquals(document.getId(), resultedDocument.getId());
        assertEquals(document.getDiploma(), resultedDocument.getDiploma());
        assertEquals(document.getIdentityCard(), resultedDocument.getIdentityCard());
    }

    @Test
    @DisplayName("Get document - not found - failure")
    void getById_notFound_failure() {
        when(documentRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> documentService.getById(ID));
    }

    @Test
    @DisplayName("Get documents - success")
    void getAll_success() {
        when(documentRepository.findAll()).thenReturn(List.of(getDocument()));

        List<Document> resultedDocuments = documentService.getAll();

        assertEquals(1, resultedDocuments.size());
    }

    @Test
    @DisplayName("Delete document - success")
    void delete_success() {
        Document document = getDocument();
        document.setUser(getUser());

        when(documentRepository.findById(ID)).thenReturn(Optional.of(document));

        documentService.deleteById(ID);

        verify(documentRepository, times(1)).delete(document);
    }

    @Test
    @DisplayName("Delete document - submitted admission file - success")
    void delete_submittedAdmissionFile_failure() {
        User user = getUser();
        user.setAdmissionFile(new AdmissionFile());
        Document document = getDocument();
        document.setUser(user);

        when(documentRepository.findById(ID)).thenReturn(Optional.of(document));

        assertThrows(ConflictException.class, () -> documentService.deleteById(ID));
    }

    private User getUser() {
        User user = new User();
        user.setEmail("dummy@gmail.com");
        user.setRole(UserRole.ROLE_STUDENT);
        user.setId(USER_ID);

        return user;
    }

    private MultipartFile getMultipartFile(boolean needsStubbing) {
        MultipartFile multipartFile = mock(MultipartFile.class);

        if (needsStubbing) {
            when(multipartFile.getContentType()).thenReturn("application/pdf");
        }

        return multipartFile;
    }

    private PutObjectResult getS3Result() {
        Map<String, String> rawMetadata = new HashMap<>();
        rawMetadata.put("Location", "dummy url");

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setUserMetadata(rawMetadata);

        PutObjectResult result = new PutObjectResult();
        result.setMetadata(metadata);

        return result;
    }

    private Document getDocument() {
        Document document = new Document();
        document.setMedicalCertificate("dummy certificate");
        document.setDiploma("dummy diploma");
        document.setIdentityCard("dummy identity card");
        document.setId(ID);

        return document;
    }
}