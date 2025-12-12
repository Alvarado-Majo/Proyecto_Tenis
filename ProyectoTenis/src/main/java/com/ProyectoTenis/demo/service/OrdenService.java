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

   
     //Crea una factura a partir del carrito:
     
    @Transactional
    public Orden procesarCompra(Carrito carrito) {

        // Traer los detalles del carrito
        List<CarritoDetalle> detalles = carritoDetalleRepository.findByCarrito(carrito);

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío, no se puede procesar la compra.");
        }

        // Crear la orden base
        Orden orden = new Orden();
        orden.setCliente(carrito.getCliente());
        orden.setFechaCreacion(LocalDateTime.now());
        orden.setEstado("PAGADA");

        // Dirección de envío (si el cliente la tiene)
        if (carrito.getCliente().getDireccion() != null) {
            orden.setDireccionEnvio(carrito.getCliente().getDireccion());
        }

        // Fecha estimada de entrega opcional (3 días después)
        orden.setFechaEstimadaEntrega(LocalDate.now().plusDays(3));

        orden = ordenRepository.save(orden);

        double total = 0.0;

        // Crear los detalles de la orden a partir de los del carrito
        for (CarritoDetalle d : detalles) {

            Tenis tenis = d.getTenis();

            // Validar stock: si es null o insuficiente -> error
            Integer stockActual = tenis.getStock();
            if (stockActual == null || stockActual < d.getCantidad()) {
                throw new IllegalStateException(
                        "No hay stock suficiente para el producto: " + tenis.getNombre()
                );
            }

            // Crear detalle de orden
            OrdenDetalle od = new OrdenDetalle();
            od.setOrden(orden);
            od.setTenis(tenis);
            od.setCantidad(d.getCantidad());
            od.setPrecioUnitario(d.getPrecioUnitario());

            double subtotal = d.getCantidad() * d.getPrecioUnitario();
            od.setSubtotal(subtotal);

            total += subtotal;

            ordenDetalleRepository.save(od);

            // Rebajar stock del tenis
            tenis.setStock(stockActual - d.getCantidad());
            tenisRepository.save(tenis);
        }

        // Setear total final en la orden
        orden.setTotal(total);
        ordenRepository.save(orden);

        // Marcar carrito como pagado y guardar total
        try {
            carrito.setEstado("PAGADO");
        } catch (Exception ignored) { }

        try {
            carrito.setTotal(total);
        } catch (Exception ignored) { }

        carritoRepository.save(carrito);

        // Vaciar carrito: borrar todos los detalles
        carritoDetalleRepository.deleteAll(detalles);

        return orden;
    }

    //Listar órdenes de un cliente (por si luego quieres historial de compras) */
    @Transactional(readOnly = true)
    public List<Orden> listarPorCliente(Cliente cliente) {
        return ordenRepository.findByCliente(cliente);
    }

    // Buscar una orden por id (para ver detalle de factura) */
    @Transactional(readOnly = true)
    public Orden buscarPorId(Long id) {
        return ordenRepository.findById(id).orElse(null);
    }
}
