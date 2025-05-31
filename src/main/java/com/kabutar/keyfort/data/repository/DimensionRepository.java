package com.kabutar.keyfort.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kabutar.keyfort.data.entity.Dimension;

public interface DimensionRepository extends JpaRepository<Dimension,String> {
    public Dimension save(Dimension dimension);

    @Query(
            value = "SELECT * FROM dimension as d WHERE d.name = :name",
            nativeQuery = true
    )
    public Dimension findByName(@Param("name") String name);
}
