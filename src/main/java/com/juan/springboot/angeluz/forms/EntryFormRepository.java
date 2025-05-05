package com.juan.springboot.angeluz.forms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EntryFormRepository extends JpaRepository<EntryForm, Long> {

    @Query("SELECT ef FROM EntryForm ef LEFT JOIN FETCH ef.mascotas WHERE ef.id = :id")
    Optional<EntryForm> findByIdWithMascotas(@Param("id") Long id);
    List<EntryForm> findByCorreo(String correo);
    int countByFechaFinGreaterThanEqual(LocalDate fecha);

    @Query("SELECT COALESCE(SUM(e.valorTotal), 0) FROM EntryForm e")
    double sumValorTotal();
    List<EntryForm> findAllByOrderByFechaInicioDesc();
}