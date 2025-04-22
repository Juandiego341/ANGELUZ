package com.juan.springboot.angeluz.forms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EntryFormRepository extends JpaRepository<EntryForm, Long> {
    @Query("SELECT e FROM EntryForm e WHERE e.fechaInicio IS NOT NULL AND e.fechaFin IS NOT NULL AND e.fechaInicio >= :startDate AND e.fechaFin <= :endDate")
    List<EntryForm> findByFechaBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    @Query("SELECT e FROM EntryForm e WHERE e.fechaInicio IS NULL OR e.fechaFin IS NULL")
    List<EntryForm> findByFechaInicioIsNullOrFechaFinIsNull();
}