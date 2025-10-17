package uis.gimnasio.servicio;

import uis.gimnasio.modelo.ClienteMembresia;
import uis.gimnasio.modelo.Membresia;

import java.util.List;

public interface IClienteMembresia {

    List<ClienteMembresia> getClienteMembresia();

    ClienteMembresia nuevaClienteMembresia(ClienteMembresia clienteMembresia);

    ClienteMembresia buscarClienteMembresia(Long id);

    int borrarClienteMembresia(Long id);
}
