package uis.gimnasio.servicio;

import uis.gimnasio.modelo.Pago;

import java.util.List;

public interface IPagoServicio {

    //Listar
    List<Pago> getPago();

    //Nuevo
    Pago nuevoPago(Pago pago);

    //Busar
    Pago buscarPago(Long id);

    //Eliminar
    int borrarPago(Long id);

}
