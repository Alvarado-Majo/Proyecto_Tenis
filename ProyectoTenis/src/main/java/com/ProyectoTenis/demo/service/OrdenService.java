package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.*;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import com.ProyectoTenis.demo.repository.CarritoRepository;
import com.ProyectoTenis.demo.repository.OrdenDetalleRepository;
import com.ProyectoTenis.demo.repository.OrdenRepository;
import com.ProyectoTenis.demo.repository.TenisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final OrdenDetalleRepository ordenDetalleRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoDetalleRepository carritoDetalleRepository;
    private final TenisRepository tenisRepository;

    public OrdenService(OrdenRepository ordenRepository,
            OrdenDetalleRepository ordenDetalleRepository,
            CarritoRepository carritoRepository,
            CarritoDetalleRepository carritoDetalleRepository,
            TenisRepository tenisRepository) {
        this.ordenRepository = ordenRepository;
        this.ordenDetalleRepository = ordenDetalleRepository;
        this.carritoRepository = carritoRepository;
        this.carritoDetalleRepository = carritoDetalleRepository;
        this.tenisRepository = tenisRepository;
    }

    
    @Transactional
    public Orden procesarCompra(Carrito carrito, MetodoPago metodoPago) {

        
        List<CarritoDetalle> detalles = carritoDetalleRepository.findByCarrito(carrito);

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío.");
        }

        Orden orden = new Orden();
        orden.setCliente(carrito.getCliente());
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setEstado("PAGADA");
        orden.setMetodoPago(metodoPago); // 🔥 AQUÍ 🔥

        if (carrito.getCliente().getDireccion() != null) {
            orden.setDireccionEnvio(carrito.getCliente().getDireccion());
        }

        orden.setFechaEstimadaEntrega(LocalDate.now().plusDays(3));

        orden = ordenRepository.save(orden);

        double total = 0.0;

        for (CarritoDetalle d : detalles) {

            Tenis tenis = d.getTenis();
            Integer stockActual = tenis.getStock();

            if (stockActual == null || stockActual < d.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente: " + tenis.getNombre());
            }

            OrdenDetalle od = new OrdenDetalle();
            od.setOrden(orden);
            od.setTenis(tenis);
            od.setCantidad(d.getCantidad());
            od.setPrecioUnitario(d.getPrecioUnitario());

            double subtotal = d.getCantidad() * d.getPrecioUnitario();
            od.setSubtotal(subtotal);

            total += subtotal;

            ordenDetalleRepository.save(od);

            tenis.setStock(stockActual - d.getCantidad());
            tenisRepository.save(tenis);
        }

        orden.setTotal(total);
        ordenRepository.save(orden);

        carrito.setEstado("PAGADO");
        carrito.setTotal(total);
        carritoRepository.save(carrito);

        carritoDetalleRepository.deleteAll(detalles);

        return orden;
    }

   
}
