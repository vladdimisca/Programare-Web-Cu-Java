package com.web.repository;

import com.web.model.ProgramOfStudy;
import com.web.model.enumeration.FinancingType;
import com.web.model.enumeration.ProgramType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProgramOfStudyRepository extends JpaRepository<ProgramOfStudy, UUID> {

    @Query("SELECT p FROM ProgramOfStudy p WHERE :type IS NULL OR p.type = :type")
    List<ProgramOfStudy> findAllByType(@Param("type") ProgramType type);

    boolean existsByNameAndFinancingType(String name, FinancingType financingType);
}
