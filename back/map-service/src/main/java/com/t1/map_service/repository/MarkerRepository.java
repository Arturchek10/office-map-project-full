package com.t1.map_service.repository;

import com.t1.map_service.model.entity.Marker;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MarkerRepository extends JpaRepository<Marker, Long> {

    @EntityGraph(attributePaths = "description")
    Optional<Marker> findById(Long id);

    // Все маркеры этажа (связь Marker - Layer - Floor
    List<Marker> findByLayer_Floor_Id(Long floorId);

    @EntityGraph(attributePaths = "description")
    @Query(
            """
            SELECT 
                m 
            FROM Marker m
            WHERE m.layer.id = :layerId
                AND (:hide = false OR m.uncomfortable = false)
            ORDER BY m.id
            """
    )
    List<Marker> findByLayerWithUncomfortableFilter(
            @Param("layerId") Long layerId,
            @Param("hide") boolean hideUncomfortable
    );

    @Query(
            """
            SELECT 
                m.layer.floor.office.id 
            FROM Marker m 
            WHERE m.id = :markerId
            """
)
    Optional<Long> findOfficeIdByMarkerId(@Param("markerId") Long markerId);
}
