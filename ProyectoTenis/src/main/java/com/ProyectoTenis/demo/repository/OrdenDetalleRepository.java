package com.ProyectoTenis.demo.repository;

import com.ProyectoTenis.demo.domain.Orden;
import com.ProyectoTenis.demo.domain.OrdenDetalle;
import com.ProyectoTenis.demo.domain.Tenis;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenDetalleRepository extends JpaRepository<OrdenDetalle, Long> {

    List<OrdenDetalle> findByOrden(Orden orden);

    OrdenDetalle findByOrdenAndTenis(Orden orden, Tenis tenis);
}
