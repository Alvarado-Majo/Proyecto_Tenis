package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Orden;
import com.ProyectoTenis.demo.domain.OrdenDetalle;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.repository.OrdenDetalleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenDetalleService {

    @Autowired
    private OrdenDetalleRepository ordenDetalleRepository;

    @Transactional(readOnly = true)
    public List<OrdenDetalle> getDetalles(Orden orden) {
        return ordenDetalleRepository.findByOrden(orden);
    }

    @Transactional(readOnly = true)
    public Optional<OrdenDetalle> getDetalle(Long idDetalle) {
        return ordenDetalleRepository.findById(idDetalle);
    }
    
    @Transactional
    public OrdenDetalle addOrUpdateItem(Orden orden, Tenis tenis, Integer cantidad, Double precioUnit) {
        if (cantidad == null || cantidad <= 0) {
            cantidad = 1;
        }
        if (precioUnit == null) {
            precioUnit = tenis.getPrecio();
        }

        OrdenDetalle existente = ordenDetalleRepository.findByOrdenAndTenis(orden, tenis);
        if (existente == null) {
            OrdenDetalle nuevo = new OrdenDetalle();
            nuevo.setOrden(orden);
            nuevo.setTenis(tenis);
            nuevo.setCantidad(cantidad);
            nuevo.setPrecioUnit(precioUnit);
            return ordenDetalleRepository.save(nuevo);
        } else {
            existente.setCantidad(existente.getCantidad() + cantidad);
            return ordenDetalleRepository.save(existente);
        }
    }

   
    @Transactional
    public void save(OrdenDetalle detalle) {
        ordenDetalleRepository.save(detalle);
    }

    @Transactional
    public void updateCantidad(Long idDetalle, int nuevaCantidad) {
        OrdenDetalle det = ordenDetalleRepository.findById(idDetalle).orElseThrow();
        if (nuevaCantidad <= 0) {
            ordenDetalleRepository.delete(det);
        } else {
            det.setCantidad(nuevaCantidad);
            ordenDetalleRepository.save(det);
        }
    }

    @Transactional
    public void delete(Long idDetalle) {
        ordenDetalleRepository.deleteById(idDetalle);
    }

    @Transactional
    public void deleteAllByOrden(Orden orden) {
        List<OrdenDetalle> items = ordenDetalleRepository.findByOrden(orden);
        ordenDetalleRepository.deleteAll(items);
    }
    @Transactional(readOnly = true)
    public double calcularTotal(Orden orden) {
        return ordenDetalleRepository.findByOrden(orden).stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnit())
                .sum();
    }
}
