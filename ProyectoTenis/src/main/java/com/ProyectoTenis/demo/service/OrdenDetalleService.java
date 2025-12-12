package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Orden;
import com.ProyectoTenis.demo.domain.OrdenDetalle;
import com.ProyectoTenis.demo.repository.OrdenDetalleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdenDetalleService {

    private final OrdenDetalleRepository ordenDetalleRepository;

    public OrdenDetalleService(OrdenDetalleRepository ordenDetalleRepository) {
        this.ordenDetalleRepository = ordenDetalleRepository;
    }

    @Transactional(readOnly = true)
    public List<OrdenDetalle> listarPorOrden(Orden orden) {
        if (orden == null || orden.getIdOrden() == null) {
            throw new IllegalArgumentException("La orden no es válida.");
        }
        return ordenDetalleRepository.findByOrden(orden);
    }

    @Transactional
    public OrdenDetalle guardar(OrdenDetalle detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle no puede ser nulo.");
        }
        return ordenDetalleRepository.save(detalle);
    }
}
