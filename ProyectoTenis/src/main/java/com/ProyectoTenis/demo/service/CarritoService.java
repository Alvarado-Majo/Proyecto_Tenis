package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.*;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import com.ProyectoTenis.demo.repository.CarritoRepository;
import com.ProyectoTenis.demo.repository.TenisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    @Autowired
    private TenisRepository tenisRepository;

    @Transactional
    public Carrito getOrCreateCarrito(Cliente cliente) {
        Carrito carrito = carritoRepository.findByClienteAndEstado(cliente, "ABIERTO");
        if (carrito == null) {
            carrito = new Carrito();
            carrito.setCliente(cliente);
            carrito.setEstado("ABIERTO");
            carritoRepository.save(carrito);
        }
        return carrito;
    }

    @Transactional
    public void agregarProducto(Carrito carrito, Long idTenis) {
        Tenis tenis = tenisRepository.findById(idTenis).orElseThrow();
        CarritoDetalle detalle = carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis);

        if (detalle == null) {
            detalle = new CarritoDetalle();
            detalle.setCarrito(carrito);
            detalle.setTenis(tenis);
            detalle.setCantidad(1);
            detalle.setPrecioUnit(tenis.getPrecio());
        } else {
            detalle.setCantidad(detalle.getCantidad() + 1);
        }

        carritoDetalleRepository.save(detalle);
    }

    @Transactional(readOnly = true)
    public List<CarritoDetalle> getDetalles(Carrito carrito) {
        return carritoDetalleRepository.findByCarrito(carrito);
    }

    @Transactional
    public void eliminarProducto(CarritoDetalle detalle) {
        carritoDetalleRepository.delete(detalle);
    }
}
