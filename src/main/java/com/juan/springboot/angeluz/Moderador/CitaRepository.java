package com.juan.springboot.angeluz.Moderador;

import com.juan.springboot.angeluz.forms.EntryForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitaRepository extends JpaRepository<EntryForm, Long> {
}