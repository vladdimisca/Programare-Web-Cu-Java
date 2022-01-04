package com.web.service;

import com.web.error.exception.ConflictException;
import com.web.error.exception.NotFoundException;
import com.web.model.ProgramOfStudy;
import com.web.model.enumeration.FinancingType;
import com.web.model.enumeration.ProgramType;
import com.web.repository.ProgramOfStudyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgramOfStudyServiceTest {

    private static final UUID ID = UUID.randomUUID();
    private static final String NAME = "test";
    private static final FinancingType FINANCING_TYPE = FinancingType.BUDGET;

    @Mock
    private ProgramOfStudyRepository programOfStudyRepository;

    @InjectMocks
    private ProgramOfStudyService programOfStudyService;

    @Test
    @DisplayName("Create program of study - success")
    void create_success() {
        ProgramOfStudy programOfStudy = getProgramOfStudy();
        ProgramOfStudy savedProgram = getSavedProgram();

        when(programOfStudyRepository.existsByNameAndFinancingType(programOfStudy.getName(), programOfStudy.getFinancingType()))
                .thenReturn(false);
        when(programOfStudyRepository.save(programOfStudy)).thenReturn(savedProgram);

        ProgramOfStudy resultedProgram = programOfStudyService.create(programOfStudy);

        assertNotNull(resultedProgram);
        assertEquals(savedProgram.getId(), resultedProgram.getId());
        assertEquals(savedProgram.getName(), resultedProgram.getName());
        assertEquals(savedProgram.getFinancingType(), resultedProgram.getFinancingType());
    }

    @Test
    @DisplayName("Create program of study - existing name and financing type - failure")
    void create_existingNameAndFinancingType_failure() {
        ProgramOfStudy programOfStudy = getProgramOfStudy();

        when(programOfStudyRepository.existsByNameAndFinancingType(programOfStudy.getName(), programOfStudy.getFinancingType()))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> programOfStudyService.create(programOfStudy));
    }

    @Test
    @DisplayName("Get program by id - success")
    void getById_success() {
        ProgramOfStudy programOfStudy = getSavedProgram();

        when(programOfStudyRepository.findById(ID)).thenReturn(Optional.of(programOfStudy));

        ProgramOfStudy resultedProgram = programOfStudyService.getById(ID);

        assertNotNull(resultedProgram);
        assertEquals(programOfStudy.getId(), resultedProgram.getId());
        assertEquals(programOfStudy.getName(), resultedProgram.getName());
        assertEquals(programOfStudy.getFinancingType(), resultedProgram.getFinancingType());
    }

    @Test
    @DisplayName("Get program by id - not found - failure")
    void getById_notFound_failure() {
        when(programOfStudyRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> programOfStudyService.getById(ID));
    }

    @Test
    @DisplayName("Update program by id - success")
    void update_success() {
        ProgramOfStudy program = getSavedProgram();
        ProgramOfStudy updatedProgram = getUpdatedProgram();

        when(programOfStudyRepository.findById(ID)).thenReturn(Optional.of(program));
        when(programOfStudyRepository.save(program)).thenReturn(updatedProgram);

        ProgramOfStudy resultedProgram = programOfStudyService.update(ID, updatedProgram);

        assertNotNull(resultedProgram);
        assertEquals(updatedProgram.getId(), resultedProgram.getId());
        assertEquals(updatedProgram.getName(), resultedProgram.getName());
        assertEquals(updatedProgram.getFinancingType(), resultedProgram.getFinancingType());
    }

    @Test
    @DisplayName("Get all programs - success")
    void getAll_success() {
        when(programOfStudyRepository.findAllByType(any())).thenReturn(List.of(getSavedProgram()));

        List<ProgramOfStudy> programs = programOfStudyService.getAll(ProgramType.BACHELORS_DEGREE);

        assertNotNull(programs);
        assertEquals(1, programs.size());
    }

    @Test
    @DisplayName("Delete program - success")
    void delete_success() {
        ProgramOfStudy program = getSavedProgram();

        when(programOfStudyRepository.findById(ID)).thenReturn(Optional.of(program));

        programOfStudyService.deleteById(ID);

        verify(programOfStudyRepository, times(1)).delete(program);
    }

    private ProgramOfStudy getProgramOfStudy() {
        ProgramOfStudy programOfStudy = new ProgramOfStudy();
        programOfStudy.setName(NAME);
        programOfStudy.setFinancingType(FINANCING_TYPE);

        return programOfStudy;
    }

    private ProgramOfStudy getSavedProgram() {
        ProgramOfStudy savedProgram = getProgramOfStudy();
        savedProgram.setId(ID);

        return savedProgram;
    }

    private ProgramOfStudy getUpdatedProgram() {
        ProgramOfStudy updatedProgram = getSavedProgram();
        updatedProgram.setName("updated");

        return updatedProgram;
    }

}