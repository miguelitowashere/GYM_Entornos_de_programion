package uis.gimnasio.service;

import uis.gimnasio.modelo.ClienteMembresia;

import java.util.List;

public interface IClienteMembresia {

    List<ClienteMembresia> getClienteMembresia();

    ClienteMembresia nuevaClienteMembresia(ClienteMembresia clienteMembresia);

    ClienteMembresia buscarClienteMembresia(Long id);

    int borrarClienteMembresia(Long id);
}
