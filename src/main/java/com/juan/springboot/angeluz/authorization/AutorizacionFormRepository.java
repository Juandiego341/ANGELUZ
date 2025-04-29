package com.juan.springboot.angeluz.authorization;

import com.juan.springboot.angeluz.forms.EntryForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutorizacionFormRepository extends JpaRepository<AutorizacionForm, Long> {
    Optional<AutorizacionForm> findByEntryForm(EntryForm entryForm);
}