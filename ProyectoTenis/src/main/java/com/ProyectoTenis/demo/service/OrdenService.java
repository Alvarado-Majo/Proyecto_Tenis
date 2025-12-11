package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.*;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import com.ProyectoTenis.demo.repository.CarritoRepository;
import com.ProyectoTenis.demo.repository.OrdenDetalleRepository;
import com.ProyectoTenis.demo.repository.OrdenRepository;
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

    public OrdenService(OrdenRepository ordenRepository,
                        OrdenDetalleRepository ordenDetalleRepository,
                        CarritoRepository carritoRepository,
                        CarritoDetalleRepository carritoDetalleRepository) {
        this.ordenRepository = ordenRepository;
        this.ordenDetalleRepository = ordenDetalleRepository;
        this.carritoRepository = carritoRepository;
        this.carritoDetalleRepository = carritoDetalleRepository;
    }

    /**
     * Crea una orden (factura) a partir del carrito:
     * - Calcula total
     * - Crea Orden y OrdenDetalle
     * - Marca el carrito como PAGADO
     * - Vacía el carrito
     */
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
        orden.setEstado("PAGADA"); // o "PENDIENTE" si quieres otro flujo

        // Opcional: usar dirección del cliente como dirección de envío
        if (carrito.getCliente().getDireccion() != null) {
            orden.setDireccionEnvio(carrito.getCliente().getDireccion());
        }

        // Opcional: fecha estimada de entrega en 3 días
        orden.setFechaEstimadaEntrega(LocalDate.now().plusDays(3));

        orden = ordenRepository.save(orden);

        double total = 0.0;

        // Crear los detalles de la orden a partir de los del carrito
        for (CarritoDetalle d : detalles) {
            OrdenDetalle od = new OrdenDetalle();
            od.setOrden(orden);
            od.setTenis(d.getTenis());
            od.setCantidad(d.getCantidad());
            od.setPrecioUnitario(d.getPrecioUnitario());

            double subtotal = d.getCantidad() * d.getPrecioUnitario();
            od.setSubtotal(subtotal);

            total += subtotal;

            ordenDetalleRepository.save(od);
        }

        // Setear total final en la orden
        orden.setTotal(total);
        ordenRepository.save(orden);

        // Marcar carrito como pagado (si tu entidad Carrito tiene este campo)
        try {
            carrito.setEstado("PAGADO");
        } catch (Exception ignored) {
            // Si tu Carrito no tiene 'estado', puedes borrar este bloque
        }

        // Guardar total del carrito si tienes ese campo
        try {
            carrito.setTotal(total);
        } catch (Exception ignored) {
            // Si tu Carrito no tiene 'total', no pasa nada
        }

        carritoRepository.save(carrito);

        // Vaciar carrito: borrar todos los detalles
        carritoDetalleRepository.deleteAll(detalles);

        return orden;
    }

    /** Listar órdenes de un cliente (por si luego quieres historial de compras) */
    @Transactional(readOnly = true)
    public List<Orden> listarPorCliente(Cliente cliente) {
        return ordenRepository.findByCliente(cliente);
    }

    /** Buscar una orden por id (para ver detalle de factura) */
    @Transactional(readOnly = true)
    public Orden buscarPorId(Long id) {
        return ordenRepository.findById(id).orElse(null);
    }
}
