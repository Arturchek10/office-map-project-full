package com.t1.map_service.security.repository;

import com.t1.map_service.security.model.OfficeAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfficeAdminRepository extends JpaRepository<OfficeAdmin, Long> {

    boolean existsByLoginAndOfficeId(String login, Long officeId);

    Optional<OfficeAdmin> findByLoginAndOfficeId(String login, Long officeId);

    @Query(
            """
            SELECT 
                oa 
            FROM OfficeAdmin oa
            WHERE oa.officeId = :officeId
            """
    )
    List<OfficeAdmin> findAllByOfficeId(@Param("officeId") Long officeId);
}
