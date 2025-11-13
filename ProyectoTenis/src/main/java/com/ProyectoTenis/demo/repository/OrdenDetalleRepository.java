package com.ProyectoTenis.demo.repository;

import com.ProyectoTenis.demo.domain.OrdenDetalle;
import com.ProyectoTenis.demo.domain.Orden;
import com.ProyectoTenis.demo.domain.Tenis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdenDetalleRepository extends JpaRepository<OrdenDetalle, Long> {

    List<OrdenDetalle> findByOrden(Orden orden);

    OrdenDetalle findByOrdenAndTenis(Orden orden, Tenis tenis);
}
