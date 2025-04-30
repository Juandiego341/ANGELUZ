package com.juan.springboot.angeluz.Admin.servicios;


import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepositorio extends JpaRepository<Servicio, Long> {
}