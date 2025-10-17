package uis.gimnasio.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.gimnasio.modelo.ClienteMembresia;
import uis.gimnasio.repositorio.ClienteMembresiaRepositorio;

import java.util.List;

@Service
@Transactional
public class ClienteMembresiaServicio implements IClienteMembresia {

    @Autowired
    ClienteMembresiaRepositorio clienteMembresiaRepositorio;

    @Override
    public List<ClienteMembresia> getClienteMembresia(){
        return clienteMembresiaRepositorio.findAll();
    }

    @Override
    public ClienteMembresia nuevaClienteMembresia(ClienteMembresia clienteMembresia) {
        return clienteMembresiaRepositorio.save(clienteMembresia);
    }

    @Override
    public ClienteMembresia buscarClienteMembresia(Long id) {
        return clienteMembresiaRepositorio.findById(id).orElse(null);
    }

    @Override
    public int borrarClienteMembresia(Long id) {
        clienteMembresiaRepositorio.deleteById(id);
        return 1;
    }


}
