package com.ProyectoTenis.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.CarritoDetalle;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;

import java.util.List;

@Service
public class CarritoDetalleService {

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    /**
     * Lista todos los productos dentro de un carrito.
     */
    public List<CarritoDetalle> listarPorCarrito(Carrito carrito) {
        return carritoDetalleRepository.findByCarrito(carrito);
    }

    /**
     * Busca un detalle del carrito por tenis.
     */
    public CarritoDetalle buscarPorCarritoYTenis(Carrito carrito, Tenis tenis) {
        return carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis);
    }

    /**
     * Agregar o actualizar un producto en el carrito.
     */
    public CarritoDetalle agregarOActualizar(Carrito carrito, Tenis tenis, int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        CarritoDetalle existente = buscarPorCarritoYTenis(carrito, tenis);

        if (existente != null) {
            // Ya existe → sumar cantidad
            existente.setCantidad(existente.getCantidad() + cantidad);
            return carritoDetalleRepository.save(existente);
        }

        // Crear nuevo detalle
        CarritoDetalle nuevo = new CarritoDetalle();
        nuevo.setCarrito(carrito);
        nuevo.setTenis(tenis);
        nuevo.setCantidad(cantidad);
        nuevo.setPrecioUnit(tenis.getPrecio());

        return carritoDetalleRepository.save(nuevo);
    }

    /**
     * Actualiza la cantidad de un item.
     */
    public CarritoDetalle actualizarCantidad(Carrito carrito, Tenis tenis, int nuevaCantidad) {

        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }

        CarritoDetalle detalle = buscarPorCarritoYTenis(carrito, tenis);

        if (detalle == null) {
            throw new IllegalArgumentException("El producto no está en el carrito.");
        }

        if (nuevaCantidad == 0) {
            carritoDetalleRepository.delete(detalle);
            return null;
        }

        detalle.setCantidad(nuevaCantidad);
        return carritoDetalleRepository.save(detalle);
    }

    /**
     * Eliminar un detalle del carrito.
     */
    public void eliminar(CarritoDetalle detalle) {
        carritoDetalleRepository.delete(detalle);
    }

    /**
     * Eliminar usando carrito + tenis.
     */
    public void eliminarPorCarritoYTenis(Carrito carrito, Tenis tenis) {
        CarritoDetalle detalle = buscarPorCarritoYTenis(carrito, tenis);
        if (detalle != null) {
            carritoDetalleRepository.delete(detalle);
        }
    }

    /**
     * Calcular subtotal (Double).
     */
    public double calcularSubtotal(CarritoDetalle detalle) {
        return detalle.getPrecioUnit() * detalle.getCantidad();
    }

    /**
     * Vaciar carrito.
     */
    public void eliminarTodoPorCarrito(Carrito carrito) {
        List<CarritoDetalle> detalles = listarPorCarrito(carrito);
        carritoDetalleRepository.deleteAll(detalles);
    }
}
