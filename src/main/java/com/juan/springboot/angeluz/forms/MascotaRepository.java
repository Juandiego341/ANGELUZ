package com.juan.springboot.angeluz.forms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    Optional<Mascota> findById(Long id);

    List<Mascota> findByRaza(String filtroRaza);
    @Query("SELECT DISTINCT m.raza FROM Mascota m ORDER BY m.raza")
    List<String> findDistinctRazas();
}