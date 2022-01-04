package com.web.repository;

import com.web.model.ProgramOfStudy;
import com.web.model.User;
import com.web.model.UserProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserProgramRepository extends JpaRepository<UserProgram, Long> {

    @Query("SELECT up FROM UserProgram up WHERE (:userId IS NULL OR up.user.id = :userId) " +
            "AND (:programId IS NULL OR up.programOfStudy.id = :programId)")
    List<UserProgram> findAllByUserAndProgram(@Param("userId") UUID userId, @Param("programId") UUID programId);

    boolean existsByUserAndProgramOfStudy(User user, ProgramOfStudy programOfStudy);
}
