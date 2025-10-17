package uis.gimnasio.servicio;

import uis.gimnasio.modelo.Membresia;

import java.util.List;

public interface IMembresiaServicio {
    //Cabecera de los metodos

    //Listar las membresias
    List<Membresia> getMembresia();

    // Nueva membresia
    Membresia nuevaMembresia(Membresia membresia);

    //Busar una membresia
    Membresia buscarMembresia(Long id);

    //Borrar membresia
    int borrarMembresia(Long id);
}
