package com.juan.springboot.angeluz.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria(String categoria);
    List<Producto> findByActivoTrue();
    Optional<Producto> findByNombre(String nombre);
    Optional<Producto> findByCodigoBarras(String codigoBarras);
    List<Producto> findByStockGreaterThanAndActivoTrue(long l);
}
