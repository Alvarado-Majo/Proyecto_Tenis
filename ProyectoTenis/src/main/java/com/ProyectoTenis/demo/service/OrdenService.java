package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.*;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import com.ProyectoTenis.demo.repository.CarritoRepository;
import com.ProyectoTenis.demo.repository.OrdenDetalleRepository;
import com.ProyectoTenis.demo.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private OrdenDetalleRepository ordenDetalleRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    @Transactional
    public Orden procesarCompra(Carrito carrito) {
        List<CarritoDetalle> detalles = carritoDetalleRepository.findByCarrito(carrito);
        double total = detalles.stream().mapToDouble(d -> d.getCantidad() * d.getPrecioUnit()).sum();

        Orden orden = new Orden();
        orden.setCliente(carrito.getCliente());
        orden.setTotal(total);
        orden.setEstado("PAGADA");
        orden.setFecha(java.time.LocalDateTime.now().toString());
        ordenRepository.save(orden);

        for (CarritoDetalle d : detalles) {
            OrdenDetalle od = new OrdenDetalle();
            od.setOrden(orden);
            od.setTenis(d.getTenis());
            od.setCantidad(d.getCantidad());
            od.setPrecioUnit(d.getPrecioUnit());
            ordenDetalleRepository.save(od);
        }

        carrito.setEstado("PAGADO");
        carritoRepository.save(carrito);

        carritoDetalleRepository.deleteAll(detalles);

        return orden;
    }
}
