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
     */
    public void guardarDetallesDesdeCarrito(List<CarritoDetalle> itemsCarrito, Orden orden) {
        for (CarritoDetalle item : itemsCarrito) {
            OrdenDetalle detalle = new OrdenDetalle();
            detalle.setOrden(orden);
            detalle.setTenis(item.getTenis());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnit(item.getPrecioUnit()); // CORREGIDO

            ordenDetalleRepository.save(detalle);
        }
    }

    /**
     * Lista todos los detalles de una orden.
     */
    public List<OrdenDetalle> listarPorOrden(Orden orden) {
        return ordenDetalleRepository.findByOrden(orden);
    }

    /**
     * Calcula el subtotal (cantidad × precioUnit)
     */
    public BigDecimal calcularSubtotal(OrdenDetalle detalle) {
        return BigDecimal.valueOf(detalle.getPrecioUnit())
                .multiply(BigDecimal.valueOf(detalle.getCantidad()));
    }

    /**
     * Calcula el total de una orden sumando todos los subtotales.
     */
    public BigDecimal calcularTotal(Orden orden) {
        List<OrdenDetalle> detalles = listarPorOrden(orden);
        return detalles.stream()
                .map(this::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Elimina todos los detalles de la orden.
     */
    public void eliminarPorOrden(Orden orden) {
        List<OrdenDetalle> detalles = listarPorOrden(orden);
        ordenDetalleRepository.deleteAll(detalles);
    }
}
