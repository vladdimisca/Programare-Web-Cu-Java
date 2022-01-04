package com.web.service;

import com.web.error.ErrorMessage;
import com.web.error.exception.ConflictException;
import com.web.error.exception.NotFoundException;
import com.web.model.ProgramOfStudy;
import com.web.model.enumeration.ProgramType;
import com.web.repository.ProgramOfStudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class ProgramOfStudyService {

    private final ProgramOfStudyRepository programOfStudyRepository;

    @Autowired
    public ProgramOfStudyService(ProgramOfStudyRepository programOfStudyRepository) {
        this.programOfStudyRepository = programOfStudyRepository;
    }

    @Transactional
    public ProgramOfStudy create(ProgramOfStudy programOfStudy) {
        checkProgramOfStudyNotExisting(programOfStudy);
        return programOfStudyRepository.save(programOfStudy);
    }

    @Transactional
    public ProgramOfStudy update(UUID id, ProgramOfStudy programOfStudy) {
        ProgramOfStudy existingProgram = getById(id);

        if (!existingProgram.getFinancingType().equals(programOfStudy.getFinancingType()) ||
                !existingProgram.getName().equals(programOfStudy.getName())) {
            checkProgramOfStudyNotExisting(programOfStudy);
        }

        copyValues(existingProgram, programOfStudy);

        return programOfStudyRepository.save(existingProgram);
    }

    public ProgramOfStudy getById(UUID id) {
        return programOfStudyRepository.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorMessage.NOT_FOUND, "program of study", id));
    }

    public List<ProgramOfStudy> getAll(ProgramType type) {
        return programOfStudyRepository.findAllByType(type);
    }

    @Transactional
    public void deleteById(UUID id) {
        programOfStudyRepository.delete(getById(id));
    }

    private void checkProgramOfStudyNotExisting(ProgramOfStudy programOfStudy) {
        if (programOfStudyRepository.existsByNameAndFinancingType(programOfStudy.getName(), programOfStudy.getFinancingType())) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "Program of study");
        }
    }

    private void copyValues(ProgramOfStudy to, ProgramOfStudy from) {
        to.setName(from.getName());
        to.setType(from.getType());
        to.setFinancingType(from.getFinancingType());
        to.setNumberOfStudents(from.getNumberOfStudents());
        to.setNumberOfYears(from.getNumberOfYears());
    }
}
