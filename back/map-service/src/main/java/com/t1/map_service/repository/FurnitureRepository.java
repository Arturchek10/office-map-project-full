package com.t1.map_service.repository;

import com.t1.map_service.model.entity.Furniture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface FurnitureRepository extends JpaRepository<Furniture, Long> {

    @Query(
    """
    SELECT 
        f 
    FROM Furniture f 
    WHERE f.id IN (
        SELECT
             MIN(ff.id)
        FROM Furniture ff
        GROUP BY LOWER(ff.name)  
    )
    """
    )
    Page<Furniture> findAllUnique(Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    @Query(
            """
            SELECT 
                f.floor.office.id
            FROM Furniture f 
            WHERE f.id = :furnitureId
            """
    )
    Optional<Long> findOfficeIdByFurnitureId(@Param("furnitureId") Long furnitureId);
}
