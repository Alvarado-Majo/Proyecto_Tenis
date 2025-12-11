package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.CarritoDetalle;
import com.ProyectoTenis.demo.domain.Cliente;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import com.ProyectoTenis.demo.repository.CarritoRepository;
import com.ProyectoTenis.demo.repository.TenisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoDetalleRepository carritoDetalleRepository;
    private final TenisRepository tenisRepository;

    public CarritoService(CarritoRepository carritoRepository,
                          CarritoDetalleRepository carritoDetalleRepository,
                          TenisRepository tenisRepository) {
        this.carritoRepository = carritoRepository;
        this.carritoDetalleRepository = carritoDetalleRepository;
        this.tenisRepository = tenisRepository;
    }

    /**
     * Obtiene el carrito ABIERTO del cliente o lo crea si no existe.
     */
    @Transactional
    public Carrito getOrCreateCarrito(Cliente cliente) {
        Carrito carrito = carritoRepository.findByClienteAndEstado(cliente, "ABIERTO");
        if (carrito == null) {
            carrito = new Carrito();
            carrito.setCliente(cliente);
            carrito.setEstado("ABIERTO");
            // fechaCreacion es LocalDateTime, no String
            carrito.setFechaCreacion(LocalDateTime.now());
            carrito = carritoRepository.save(carrito);
        }
        return carrito;
    }

    /**
     * Agrega un tenis al carrito. Si ya existe en el carrito, solo aumenta la cantidad.
     */
    @Transactional
    public void agregarProducto(Carrito carrito, Long idTenis) {
        Tenis tenis = tenisRepository.findById(idTenis)
                .orElseThrow(() -> new IllegalArgumentException("Tenis no encontrado"));

        // ¿Ya existe este tenis en el carrito?
        CarritoDetalle detalle = carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis);

        if (detalle == null) {
            detalle = new CarritoDetalle();
            detalle.setCarrito(carrito);
            detalle.setTenis(tenis);
            detalle.setCantidad(1);
            // usamos precioUnitario
            detalle.setPrecioUnitario(tenis.getPrecio());
            // si quieres, puedes calcular el subtotal aquí también:
            detalle.setSubtotal(tenis.getPrecio());
        } else {
            detalle.setCantidad(detalle.getCantidad() + 1);
            // actualizar subtotal
            detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
        }

        carritoDetalleRepository.save(detalle);
    }

    /**
     * Devuelve el carrito ABIERTO del cliente (o null si no tiene).
     */
    @Transactional(readOnly = true)
    public Carrito obtenerCarritoDeCliente(Cliente cliente) {
        return carritoRepository.findByClienteAndEstado(cliente, "ABIERTO");
    }

    /**
     * Calcula el total del carrito del cliente (sumatoria de cantidad * precioUnitario).
     */
    @Transactional(readOnly = true)
    public double calcularTotal(Cliente cliente) {
        Carrito carrito = obtenerCarritoDeCliente(cliente);
        if (carrito == null) {
            return 0.0;
        }

        List<CarritoDetalle> detalles = carritoDetalleRepository.findByCarrito(carrito);
        return detalles.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();
    }

    /**
     * Elimina una línea del carrito (detalle) del cliente.
     */
    @Transactional
    public void eliminarProducto(Cliente cliente, Long idDetalle) {
        Carrito carrito = obtenerCarritoDeCliente(cliente);
        if (carrito == null) {
            return;
        }

        carritoDetalleRepository.findById(idDetalle).ifPresent(detalle -> {
            if (detalle.getCarrito() != null
                    && detalle.getCarrito().getIdCarrito().equals(carrito.getIdCarrito())) {
                carritoDetalleRepository.delete(detalle);
            }
        });
    }

    /**
     * Lista los detalles de un carrito.
     */
    @Transactional(readOnly = true)
    public List<CarritoDetalle> getDetalles(Carrito carrito) {
        return carritoDetalleRepository.findByCarrito(carrito);
    }
}
