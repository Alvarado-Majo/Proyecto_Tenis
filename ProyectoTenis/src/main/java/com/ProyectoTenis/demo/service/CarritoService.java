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

    @Transactional
    public Carrito getOrCreateCarrito(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) {
            throw new IllegalArgumentException("El cliente no es válido.");
        }

        Carrito carrito = carritoRepository.findByClienteAndEstado(cliente, "ABIERTO");
        if (carrito == null) {
            carrito = new Carrito();
            carrito.setCliente(cliente);
            carrito.setEstado("ABIERTO");
            carrito.setFechaCreacion(LocalDateTime.now());
            carrito = carritoRepository.save(carrito);
        }
        return carrito;
    }

    
    @Transactional
    public void agregarProducto(Carrito carrito, Long idTenis) {
        agregarProducto(carrito, idTenis, 1);
    }

    @Transactional
    public void agregarProducto(Carrito carrito, Long idTenis, int cantidad) {
        if (carrito == null || carrito.getIdCarrito() == null) {
            throw new IllegalArgumentException("El carrito no es válido.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        Tenis tenis = tenisRepository.findById(idTenis)
                .orElseThrow(() -> new IllegalArgumentException("Tenis no encontrado."));

        Integer stock = tenis.getStock();
        if (stock == null || stock <= 0) {
            throw new IllegalStateException("Este producto no tiene stock disponible.");
        }

        CarritoDetalle detalle = carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis);

        int cantidadActual = (detalle != null && detalle.getCantidad() != null)
                ? detalle.getCantidad()
                : 0;

        int nuevaCantidad = cantidadActual + cantidad;

        if (nuevaCantidad > stock) {
            throw new IllegalStateException(
                    "No hay suficiente stock disponible. Stock actual: " + stock
            );
        }

        if (detalle == null) {
            detalle = new CarritoDetalle();
            detalle.setCarrito(carrito);
            detalle.setTenis(tenis);
            detalle.setCantidad(nuevaCantidad);
            detalle.setPrecioUnitario(tenis.getPrecio());
        } else {
            detalle.setCantidad(nuevaCantidad);
        }

        detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());

        carritoDetalleRepository.save(detalle);
    }

    @Transactional(readOnly = true)
    public Carrito obtenerCarritoDeCliente(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null) {
            return null;
        }
        return carritoRepository.findByClienteAndEstado(cliente, "ABIERTO");
    }

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

    @Transactional(readOnly = true)
    public List<CarritoDetalle> getDetalles(Carrito carrito) {
        if (carrito == null || carrito.getIdCarrito() == null) {
            throw new IllegalArgumentException("El carrito no es válido.");
        }
        return carritoDetalleRepository.findByCarrito(carrito);
    }
}
