package com.juan.springboot.angeluz.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria(String categoria);

    Optional<Producto> findByNombre(String nombre);
}
