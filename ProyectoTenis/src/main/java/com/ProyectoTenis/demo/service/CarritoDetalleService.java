package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.CarritoDetalle;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarritoDetalleService {

    private final CarritoDetalleRepository carritoDetalleRepository;

    public CarritoDetalleService(CarritoDetalleRepository carritoDetalleRepository) {
        this.carritoDetalleRepository = carritoDetalleRepository;
    }

    public List<CarritoDetalle> listarPorCarrito(Carrito carrito) {
        return carritoDetalleRepository.findByCarrito(carrito);
    }

    public CarritoDetalle buscarPorCarritoYTenis(Carrito carrito, Tenis tenis) {
        // aquí ya NO usamos orElse(null)
        return carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis);
    }

    public CarritoDetalle guardar(CarritoDetalle detalle) {
        return carritoDetalleRepository.save(detalle);
    }

    public void eliminar(Long idCarritoDetalle) {
        carritoDetalleRepository.deleteById(idCarritoDetalle);
    }
}
