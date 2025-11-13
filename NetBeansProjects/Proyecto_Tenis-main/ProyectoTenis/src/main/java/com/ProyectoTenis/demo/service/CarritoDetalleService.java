package com.ProyectoTenis.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ProyectoTenis.demo.domain.*;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoDetalleService {

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    /**
     * Lista todos los productos dentro del carrito.
     */
    public List<CarritoDetalle> listarPorCarrito(Carrito carrito) {
        return carritoDetalleRepository.findByCarrito(carrito);
    }

    /**
     * Busca un detalle por carrito + tenis.
     */
    public Optional<CarritoDetalle> buscarPorCarritoYTenis(Carrito carrito, Tenis tenis) {
        return Optional.ofNullable(
                carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis)
        );
    }

    /**
     * Agrega o actualiza un producto en el carrito.
     */
    public CarritoDetalle agregarOActualizar(Carrito carrito, Tenis tenis, int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        Optional<CarritoDetalle> existenteOpt = buscarPorCarritoYTenis(carrito, tenis);

        if (existenteOpt.isPresent()) {
            CarritoDetalle existente = existenteOpt.get();
            existente.setCantidad(existente.getCantidad() + cantidad);
            return carritoDetalleRepository.save(existente);
        }

        // Nuevo detalle
        CarritoDetalle nuevo = new CarritoDetalle();
        nuevo.setCarrito(carrito);
        nuevo.setTenis(tenis);
        nuevo.setCantidad(cantidad);
        nuevo.setPrecioUnit(tenis.getPrecio()); // BigDecimal ✔

        return carritoDetalleRepository.save(nuevo);
    }

    /**
     * Actualiza la cantidad de un item.
     */
    public CarritoDetalle actualizarCantidad(Carrito carrito, Tenis tenis, int nuevaCantidad) {

        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }

        CarritoDetalle detalle = buscarPorCarritoYTenis(carrito, tenis)
                .orElseThrow(() -> new IllegalArgumentException("El producto no está en el carrito."));

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
        buscarPorCarritoYTenis(carrito, tenis)
                .ifPresent(carritoDetalleRepository::delete);
    }

    /**
     * Calcular subtotal correctamente con BigDecimal.
     */
    public BigDecimal calcularSubtotal(CarritoDetalle detalle) {
        return detalle.getPrecioUnit() // BigDecimal
                .multiply(BigDecimal.valueOf(detalle.getCantidad()));
    }

    /**
     * Vaciar carrito.
     */
    public void eliminarTodoPorCarrito(Carrito carrito) {
        List<CarritoDetalle> detalles = listarPorCarrito(carrito);
        carritoDetalleRepository.deleteAll(detalles);
    }
}
