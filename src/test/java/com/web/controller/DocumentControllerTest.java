package com.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dto.DocumentDto;
import com.web.mapper.DocumentMapper;
import com.web.model.Document;
import com.web.repository.UserRepository;
import com.web.service.DocumentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DocumentController.class)
class DocumentControllerTest {

    private static final Long ID = 1L;
    private static final UUID USER_ID = UUID.randomUUID();

    @MockBean
    private DocumentService documentService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DocumentMapper documentMapper;

    @Test
    @DisplayName("Create document - success")
    @WithMockUser(roles = {"STUDENT"})
    void createDocument_success() throws Exception {
        MockMultipartFile identityCard = getMultipartFile("identity-card", "identityCard.pdf");
        MockMultipartFile medicalCertificate = getMultipartFile("medical-certificate", "medicalCertificate.pdf");
        MockMultipartFile diploma = getMultipartFile("diploma", "diploma.pdf");
        Document document = getDocument();
        DocumentDto documentDto = getDocumentDto();

        when(documentService.create(identityCard, medicalCertificate, diploma)).thenReturn(document);
        when(documentMapper.mapToDto(document)).thenReturn(documentDto);

        mockMvc.perform(multipart("/api/documents/")
                    .file(identityCard)
                    .file(medicalCertificate)
                    .file(diploma)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Update document - success")
    @WithMockUser(roles = {"STUDENT"})
    void updateDocument_success() throws Exception {
        MockMultipartFile identityCard = getMultipartFile("identity-card", "identityCard.pdf");
        MockMultipartFile medicalCertificate = getMultipartFile("medical-certificate", "medicalCertificate.pdf");
        MockMultipartFile diploma = getMultipartFile("diploma", "diploma.pdf");
        Document document = getDocument();
        DocumentDto documentDto = getDocumentDto();

        when(documentService.update(ID, identityCard, medicalCertificate, diploma)).thenReturn(document);
        when(documentMapper.mapToDto(document)).thenReturn(documentDto);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload("/api/documents/" + ID);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder
                        .file(identityCard)
                        .file(medicalCertificate)
                        .file(diploma)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get document - success")
    @WithMockUser(roles = {"STUDENT"})
    void getDocument_success() throws Exception {
        DocumentDto documentDto = getDocumentDto();
        Document document = getDocument();

        when(documentService.getById(ID)).thenReturn(document);
        when(documentMapper.mapToDto(document)).thenReturn(documentDto);

        mockMvc.perform(get("/api/documents/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(documentDto.id()));
    }

    @Test
    @DisplayName("Get all documents - success")
    @WithMockUser(roles = {"ADMIN"})
    void getAllDocument_success() throws Exception {
        DocumentDto documentDto = getDocumentDto();
        Document document = getDocument();

        when(documentService.getAll()).thenReturn(List.of(document));
        when(documentMapper.mapToDto(document)).thenReturn(documentDto);

        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(documentDto.id()));
    }

    @Test
    @DisplayName("Delete document - success")
    @WithMockUser(roles = {"STUDENT"})
    void deleteDocument_success() throws Exception {
        mockMvc.perform(delete("/api/documents/" + ID))
                .andExpect(status().isNoContent());

        verify(documentService, times(1)).deleteById(ID);
    }

    private MockMultipartFile getMultipartFile(String file, String fileName) {
        return new MockMultipartFile(
                file,
                fileName,
                MediaType.TEXT_PLAIN_VALUE,
                "dummy".getBytes(StandardCharsets.UTF_8)
        );
    }

    private DocumentDto getDocumentDto() {
        return new DocumentDto(ID, "dummy id", "dummy mc", "dummy diploma", USER_ID);
    }

    private Document getDocument() {
        Document document = new Document();
        document.setId(ID);
        document.setIdentityCard("dummy id");
        document.setMedicalCertificate("dummy mc");
        document.setDiploma("dummy diploma");

        return document;
    }

}