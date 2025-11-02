package com.ProyectoTenis.demo.service;

import com.ProyectoTenis.demo.domain.Carrito;
import com.ProyectoTenis.demo.domain.CarritoDetalle;
import com.ProyectoTenis.demo.domain.Tenis;
import com.ProyectoTenis.demo.repository.CarritoDetalleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoDetalleService {

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    @Transactional(readOnly = true)
    public List<CarritoDetalle> getDetalles(Carrito carrito) {
        return carritoDetalleRepository.findByCarrito(carrito);
    }

    @Transactional(readOnly = true)
    public Optional<CarritoDetalle> getDetalle(Long idDetalle) {
        return carritoDetalleRepository.findById(idDetalle);
    }

    @Transactional(readOnly = true)
    public CarritoDetalle getItemEnCarrito(Carrito carrito, Tenis tenis) {
        return carritoDetalleRepository.findByCarritoAndTenis(carrito, tenis);
    }

    @Transactional
    public void save(CarritoDetalle detalle) {
        carritoDetalleRepository.save(detalle);
    }

    @Transactional
    public void actualizarCantidad(Long idDetalle, int nuevaCantidad) {
        CarritoDetalle det = carritoDetalleRepository.findById(idDetalle).orElseThrow();
        if (nuevaCantidad <= 0) {
            carritoDetalleRepository.delete(det);
        } else {
            det.setCantidad(nuevaCantidad);
            carritoDetalleRepository.save(det);
        }
    }
    
    @Transactional
    public void delete(Long idDetalle) {
        carritoDetalleRepository.deleteById(idDetalle);
    }

    @Transactional
    public void deleteAllByCarrito(Carrito carrito) {
        carritoDetalleRepository.deleteAll(getDetalles(carrito));
    }
}
