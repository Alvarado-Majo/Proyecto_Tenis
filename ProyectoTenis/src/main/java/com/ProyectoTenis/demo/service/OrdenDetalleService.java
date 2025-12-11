package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Orden;
import com.ProyectoTenis.demo.domain.OrdenDetalle;
import com.ProyectoTenis.demo.repository.OrdenDetalleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdenDetalleService {

    private final OrdenDetalleRepository ordenDetalleRepository;

    public OrdenDetalleService(OrdenDetalleRepository ordenDetalleRepository) {
        this.ordenDetalleRepository = ordenDetalleRepository;
    }

    public List<OrdenDetalle> listarPorOrden(Orden orden) {
        return ordenDetalleRepository.findByOrden(orden);
    }

    public OrdenDetalle guardar(OrdenDetalle detalle) {
        return ordenDetalleRepository.save(detalle);
    }
}
