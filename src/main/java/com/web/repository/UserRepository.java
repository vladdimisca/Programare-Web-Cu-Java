package com.web.repository;

import com.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN UserInfo ui ON u.userInfo.id = ui.id " +
            "WHERE (:email IS NULL OR u.email = :email) " +
            "AND (:nationality IS NULL OR ui.nationality = :nationality)")
    List<User> findAllByEmailAndNationality(@Param("email") String email, @Param("nationality") String nationality);
}
