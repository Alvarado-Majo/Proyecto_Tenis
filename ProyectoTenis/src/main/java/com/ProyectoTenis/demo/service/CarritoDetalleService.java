package com.ProyectoTenis.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.CarritoDetalle;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoDetalleService {

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    /**
     * Lista todos los productos dentro de un carrito específico.
     */
    public List<CarritoDetalle> listarPorCarrito(Carrito carrito) {
        return carritoDetalleRepository.findByCarrito(carrito);
    }

    /**
     * Busca un ítem del carrito por el producto (Tenis).
     */
    public Optional<CarritoDetalle> buscarPorCarritoYTenis(Carrito carrito, Tenis tenis) {
        return carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis);
    }

    /**
     * Agrega un producto nuevo al carrito o actualiza su cantidad si ya existe.
     */
    public CarritoDetalle agregarOActualizar(Carrito carrito, Tenis tenis, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        Optional<CarritoDetalle> existenteOpt = buscarPorCarritoYTenis(carrito, tenis);
        CarritoDetalle detalle;

        if (existenteOpt.isPresent()) {
            detalle = existenteOpt.get();
            detalle.setCantidad(detalle.getCantidad() + cantidad);
        } else {
            detalle = new CarritoDetalle();
            detalle.setCarrito(carrito);
            detalle.setTenis(tenis);
            detalle.setCantidad(cantidad);
            detalle.setPrecio_unit(tenis.getPrecio());
        }

        return carritoDetalleRepository.save(detalle);
    }

    /**
     * Actualiza la cantidad de un producto específico.
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
     * Elimina un producto del carrito.
     */
    public void eliminar(CarritoDetalle detalle) {
        carritoDetalleRepository.delete(detalle);
    }

    /**
     * Elimina un producto específico usando carrito + tenis.
     */
    public void eliminarPorCarritoYTenis(Carrito carrito, Tenis tenis) {
        buscarPorCarritoYTenis(carrito, tenis)
                .ifPresent(carritoDetalleRepository::delete);
    }

    /**
     * Calcula el subtotal de un detalle (cantidad * precio_unit).
     */
    public BigDecimal calcularSubtotal(CarritoDetalle detalle) {
        return detalle.getPrecio_unit().multiply(BigDecimal.valueOf(detalle.getCantidad()));
    }

    /**
     * Elimina todos los ítems de un carrito (vaciar carrito).
     */
    public void eliminarTodoPorCarrito(Carrito carrito) {
        List<CarritoDetalle> detalles = listarPorCarrito(carrito);
        carritoDetalleRepository.deleteAll(detalles);
    }
}
