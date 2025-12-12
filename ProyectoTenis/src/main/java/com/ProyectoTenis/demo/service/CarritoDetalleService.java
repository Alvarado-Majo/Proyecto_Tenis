package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.CarritoDetalle;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarritoDetalleService {

    private final CarritoDetalleRepository carritoDetalleRepository;

    public CarritoDetalleService(CarritoDetalleRepository carritoDetalleRepository) {
        this.carritoDetalleRepository = carritoDetalleRepository;
    }

    @Transactional(readOnly = true)
    public List<CarritoDetalle> listarPorCarrito(Carrito carrito) {
        if (carrito == null || carrito.getIdCarrito() == null) {
            throw new IllegalArgumentException("El carrito no es válido.");
        }
        return carritoDetalleRepository.findByCarrito(carrito);
    }

    @Transactional(readOnly = true)
    public CarritoDetalle buscarPorCarritoYTenis(Carrito carrito, Tenis tenis) {
        if (carrito == null || tenis == null) {
            return null;
        }
        return carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis);
    }

    @Transactional
    public CarritoDetalle guardar(CarritoDetalle detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle del carrito no puede ser nulo.");
        }
        return carritoDetalleRepository.save(detalle);
    }

   
    @Transactional
    public void eliminar(Long idCarritoDetalle) {
        if (idCarritoDetalle == null) {
            throw new IllegalArgumentException("El ID del detalle no puede ser nulo.");
        }
        carritoDetalleRepository.deleteById(idCarritoDetalle);
    }
}
