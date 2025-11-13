package com.ProyectoTenis.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ProyectoTenis.demo.domain.Orden;
import com.ProyectoTenis.demo.domain.OrdenDetalle;
import com.ProyectoTenis.demo.domain.CarritoDetalle;
import com.ProyectoTenis.demo.repository.OrdenDetalleRepository;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrdenDetalleService {

    @Autowired
    private OrdenDetalleRepository ordenDetalleRepository;

    /**
     * Guarda todos los detalles de una orden a partir del carrito del cliente.
     * Cada producto del carrito se convierte en una línea en la orden.
     */
    public void guardarDetallesDesdeCarrito(List<CarritoDetalle> itemsCarrito, Orden orden) {
        for (CarritoDetalle item : itemsCarrito) {
            OrdenDetalle detalle = new OrdenDetalle();
            detalle.setOrden(orden);
            detalle.setTenis(item.getTenis());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio_unit(item.getPrecio_unit());
            // subtotal calculado automáticamente por la BD o manualmente:
            // detalle.setSubtotal(item.getPrecio_unit().multiply(BigDecimal.valueOf(item.getCantidad())));
            ordenDetalleRepository.save(detalle);
        }
    }

    /**
     * Lista todos los detalles de una orden específica.
     */
    public List<OrdenDetalle> listarPorOrden(Orden orden) {
        return ordenDetalleRepository.findByOrden(orden);
    }

    /**
     * Calcula el subtotal (cantidad * precio_unit) de un detalle.
     */
    public BigDecimal calcularSubtotal(OrdenDetalle detalle) {
        return detalle.getPrecio_unit().multiply(BigDecimal.valueOf(detalle.getCantidad()));
    }

    /**
     * Calcula el total de una orden (sumatoria de subtotales).
     */
    public BigDecimal calcularTotal(Orden orden) {
        List<OrdenDetalle> detalles = listarPorOrden(orden);
        return detalles.stream()
                .map(this::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Elimina todos los detalles de una orden (por ejemplo, si se cancela).
     */
    public void eliminarPorOrden(Orden orden) {
        List<OrdenDetalle> detalles = listarPorOrden(orden);
        ordenDetalleRepository.deleteAll(detalles);
    }
}
