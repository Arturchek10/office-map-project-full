package com.t1.map_service.repository;

import com.t1.map_service.model.entity.Layer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LayerRepository extends JpaRepository<Layer, Long> {
    boolean existsByNameIgnoreCaseAndFloorId(String name, Long floorId);

    boolean existsByFloorIdAndBaseTrue(Long floorId);

    List<Layer> findByFloorIdOrderByNameAsc(Long floorId);
    Optional<Layer> findByFloorIdAndBaseTrue(Long floorId);

    @Query(
            """
            SELECT 
                l.floor.office.id 
            FROM Layer l 
            WHERE l.id = :layerId
            """
    )
    Optional<Long> findOfficeIdByLayerId(@Param("layerId") Long layerId);

}
