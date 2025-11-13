package com.ProyectoTenis.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ProyectoTenis.demo.domain.*;
import com.ProyectoTenis.demo.repository.CarritoRepository;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import com.ProyectoTenis.demo.repository.TenisRepository;

import java.util.List;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    @Autowired
    private TenisRepository tenisRepository;

    /**
     * Obtener carrito activo. Si no existe, crearlo.
     */
    public Carrito obtenerCarritoActivo(Cliente cliente) {

        Carrito carrito = carritoRepository.findByClienteAndEstado(cliente, "ABIERTO");

        if (carrito == null) {
            carrito = new Carrito();
            carrito.setCliente(cliente);
            carrito.setEstado("ABIERTO");
            carritoRepository.save(carrito);
        }

        return carrito;
    }

    /**
     * Agregar producto al carrito.
     */
    public void agregarProducto(Cliente cliente, Integer idTenis, int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        Carrito carrito = obtenerCarritoActivo(cliente);

        Tenis tenis = tenisRepository.findById(Long.valueOf(idTenis))
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        // Buscar si ya existe el producto en el carrito
        CarritoDetalle existente = carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis);

        if (existente != null) {
            // Si existe, sumar cantidad
            existente.setCantidad(existente.getCantidad() + cantidad);
            carritoDetalleRepository.save(existente);
        } else {
            // Si no existe, crear nuevo detalle
            CarritoDetalle nuevo = new CarritoDetalle();
            nuevo.setCarrito(carrito);
            nuevo.setTenis(tenis);
            nuevo.setCantidad(cantidad);
            nuevo.setPrecioUnit(tenis.getPrecio());
            carritoDetalleRepository.save(nuevo);
        }
    }

    /**
     * Eliminar un producto del carrito.
     */
    public void eliminarProducto(Cliente cliente, Integer idTenis) {

        Carrito carrito = obtenerCarritoActivo(cliente);

        Tenis tenis = tenisRepository.findById(Long.valueOf(idTenis))
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        CarritoDetalle detalle = carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis);

        if (detalle != null) {
            carritoDetalleRepository.delete(detalle);
        }
    }

    /**
     * Listar productos del carrito.
     */
    public List<CarritoDetalle> listarProductos(Cliente cliente) {
        Carrito carrito = obtenerCarritoActivo(cliente);
        return carritoDetalleRepository.findByCarrito(carrito);
    }

    /**
     * Calcular total con Double.
     */
    public double calcularTotal(Cliente cliente) {
        return listarProductos(cliente).stream()
                .mapToDouble(cd -> cd.getPrecioUnit() * cd.getCantidad())
                .sum();
    }

    /**
     * Cerrar carrito.
     */
    public Carrito cerrarCarrito(Cliente cliente) {
        Carrito carrito = obtenerCarritoActivo(cliente);
        carrito.setEstado("PAGADO");
        return carritoRepository.save(carrito);
    }

    /**
     * Vaciar carrito.
     */
    public void vaciarCarrito(Cliente cliente) {
        Carrito carrito = obtenerCarritoActivo(cliente);
        List<CarritoDetalle> detalles = carritoDetalleRepository.findByCarrito(carrito);
        carritoDetalleRepository.deleteAll(detalles);
    }
}
