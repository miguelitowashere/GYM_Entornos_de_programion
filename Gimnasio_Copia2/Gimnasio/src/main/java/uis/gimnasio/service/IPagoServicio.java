package uis.gimnasio.service;

import uis.gimnasio.modelo.Pago;
import java.util.List;

// Debe existir y estar en el paquete correcto
public interface IPagoServicio {

    // Debe coincidir con el método usado en PagoControlador
    public List<Pago> listarPagos();

    // Debe coincidir con el método usado en PagoControlador
    public Pago buscarPago(Long idPago);

    // Debe coincidir con el método usado en PagoControlador
    public Pago guardarPago(Pago pago);

    // Debe coincidir con el método usado en PagoControlador
    public void eliminarPago(Long idPago);
}