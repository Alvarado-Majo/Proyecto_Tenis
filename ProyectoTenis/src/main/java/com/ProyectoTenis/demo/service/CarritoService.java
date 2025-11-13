package com.ProyectoTenis.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ProyectoTenis.demo.domain.*;
import com.ProyectoTenis.demo.repository.CarritoRepository;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import com.ProyectoTenis.demo.repository.TenisRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    @Autowired
    private TenisRepository tenisRepository;

    /**
     * Obtiene el carrito activo del cliente.
     * Si no existe, crea uno nuevo automáticamente.
     */
    public Carrito obtenerCarritoActivo(Cliente cliente) {
        List<Carrito> carritos = carritoRepository.findByClienteAndEstado(cliente, "ABIERTO");
        if (carritos.isEmpty()) {
            Carrito nuevo = new Carrito();
            nuevo.setCliente(cliente);
            nuevo.setEstado("ABIERTO");
            return carritoRepository.save(nuevo);
        }
        return carritos.get(0);
    }

    /**
     * Agrega un producto (tenis) al carrito activo.
     * Si ya existe, aumenta la cantidad.
     */
    public void agregarProducto(Cliente cliente, Integer idTenis, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        Carrito carrito = obtenerCarritoActivo(cliente);
        Tenis tenis = tenisRepository.findById(idTenis)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        Optional<CarritoDetalle> existenteOpt = carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis);

        if (existenteOpt.isPresent()) {
            CarritoDetalle existente = existenteOpt.get();
            existente.setCantidad(existente.getCantidad() + cantidad);
            carritoDetalleRepository.save(existente);
        } else {
            CarritoDetalle nuevo = new CarritoDetalle();
            nuevo.setCarrito(carrito);
            nuevo.setTenis(tenis);
            nuevo.setCantidad(cantidad);
            nuevo.setPrecio_unit(tenis.getPrecio());
            carritoDetalleRepository.save(nuevo);
        }
    }

    /**
     * Elimina un producto del carrito activo.
     */
    public void eliminarProducto(Cliente cliente, Integer idTenis) {
        Carrito carrito = obtenerCarritoActivo(cliente);
        Tenis tenis = tenisRepository.findById(idTenis)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis)
                .ifPresent(carritoDetalleRepository::delete);
    }

    /**
     * Obtiene los productos del carrito activo.
     */
    public List<CarritoDetalle> listarProductos(Cliente cliente) {
        Carrito carrito = obtenerCarritoActivo(cliente);
        return carritoDetalleRepository.findByCarrito(carrito);
    }

    /**
     * Calcula el total del carrito.
     */
    public BigDecimal calcularTotal(Cliente cliente) {
        return listarProductos(cliente).stream()
                .map(cd -> cd.getPrecio_unit().multiply(BigDecimal.valueOf(cd.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Cierra el carrito (marcar como PAGADO).
     */
    public Carrito cerrarCarrito(Cliente cliente) {
        Carrito carrito = obtenerCarritoActivo(cliente);
        carrito.setEstado("PAGADO");
        return carritoRepository.save(carrito);
    }

    /**
     * Elimina todo el contenido del carrito actual (opcional).
     */
    public void vaciarCarrito(Cliente cliente) {
        Carrito carrito = obtenerCarritoActivo(cliente);
        List<CarritoDetalle> detalles = carritoDetalleRepository.findByCarrito(carrito);
        carritoDetalleRepository.deleteAll(detalles);
    }
}
