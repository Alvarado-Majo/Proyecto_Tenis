package com.ProyectoTenis.demo.repository;

import com.ProyectoTenis.demo.domain.CarritoDetalle;
import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.Tenis;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoDetalleRepository extends JpaRepository<CarritoDetalle, Long> {

    List<CarritoDetalle> findByCarrito(Carrito carrito);

    CarritoDetalle findByCarritoAndTenis(Carrito carrito, Tenis tenis);
}
