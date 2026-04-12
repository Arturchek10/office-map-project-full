package com.t1.map_service.repository;

import com.t1.map_service.model.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FloorRepository extends JpaRepository<Floor, Long> {
    boolean existsByOfficeIdAndOrderNumber(Long officeId, int orderNumber);

    @Query(
            """
            SELECT 
                f.office.id
            FROM Floor f 
            WHERE f.id = :floorId
            """
    )
    Optional<Long> findOfficeIdByFloorId(@Param("floorId") Long floorId);
}
