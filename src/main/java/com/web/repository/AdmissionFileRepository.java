package com.web.repository;

import com.web.model.AdmissionFile;
import com.web.model.User;
import com.web.model.enumeration.AdmissionFileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdmissionFileRepository extends JpaRepository<AdmissionFile, Long> {

    @Query("SELECT af FROM AdmissionFile af WHERE (:userId IS NULL OR af.user.id = :userId) " +
            "AND (:status IS NULL OR af.status = :status)")
    List<AdmissionFile> findAllByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") AdmissionFileStatus status);
}
