package uis.gimnasio.service;

import uis.gimnasio.modelo.Pago;
import uis.gimnasio.repositorio.PagoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

// 1. ANOTACIÓN CLAVE: Identifica esta clase como el componente de servicio de Spring
@Service
@Transactional
public class PagoServicioImpl implements IPagoServicio { // Implementa la interfaz

    // 2. INYECCIÓN DEL REPOSITORIO
    @Autowired
    private PagoRepositorio pagoRepositorio;

    // 3. IMPLEMENTACIÓN DE MÉTODOS REQUERIDOS

    @Override
    public List<Pago> listarPagos() {
        return pagoRepositorio.findAll();
    }

    @Override
    public Pago buscarPago(Long idPago) {
        // Intenta obtener el pago o devuelve null si no existe.
        try {
            return pagoRepositorio.findById(idPago).orElse(null);
        } catch (Exception e) {
            // Manejo de error para evitar que la aplicación se caiga si el ID es nulo.
            return null;
        }
    }

    @Override
    public Pago guardarPago(Pago pago) {
        return pagoRepositorio.save(pago);
    }

    @Override
    public void eliminarPago(Long idPago) {
        pagoRepositorio.deleteById(idPago);
    }
}