package com.t1.map_service.repository;

import com.t1.map_service.model.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OfficeRepository extends JpaRepository<Office, Long> {
    boolean existsByAddressIgnoreCase(String address);

    boolean existsByAddressIgnoreCaseAndIdNot(String address, Long id);

    @Query("""
    SELECT DISTINCT
         o
    FROM Office o 
    LEFT JOIN FETCH o.floors f 
    WHERE o.id = :id
    ORDER BY f.orderNumber
    """)
    Optional<Office> findByIdWithFloorsOrdered(@Param("id") Long id);
}
