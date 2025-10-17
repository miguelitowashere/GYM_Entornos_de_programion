package uis.gimnasio.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.gimnasio.modelo.ClienteMembresia;
import uis.gimnasio.repositorio.ClienteMembresiaRepositorio;
import uis.gimnasio.servicio.ClienteMembresiaServicio;

import java.util.List;

@RestController
@RequestMapping("/clientemembresia")
public class ClienteMembresiaControlador {

    //Atributos
    @Autowired
    ClienteMembresiaServicio clienteMembresiaServicio;

    //Listar cliente_membresia
    @GetMapping("/list")
    public List<ClienteMembresia> cargarClienteMembresia(){
        return clienteMembresiaServicio.getClienteMembresia();
    }

    //Buscar por ID
    @GetMapping("/list/{id}")
    public ClienteMembresia buscarPorId(@PathVariable Long id){
        return clienteMembresiaServicio.buscarClienteMembresia(id);
    }

    //Agregar
    @PostMapping("/")
    public ResponseEntity<ClienteMembresia> agregar(@RequestBody ClienteMembresia clienteMembresia){
        ClienteMembresia obj = clienteMembresiaServicio.nuevaClienteMembresia(clienteMembresia);
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    //Editar
    @PutMapping("/")
    public ResponseEntity<ClienteMembresia> editar(@RequestBody ClienteMembresia clienteMembresia){
        ClienteMembresia obj = clienteMembresiaServicio.buscarClienteMembresia(clienteMembresia.getId());
        if (obj != null) {
            obj.setUsuario(clienteMembresia.getUsuario());
            obj.setMembresia(clienteMembresia.getMembresia());
            obj.setFechaInicio(clienteMembresia.getFechaInicio());
            obj.setFechaFin(clienteMembresia.getFechaFin());
            obj.setEstado(clienteMembresia.getEstado());

            clienteMembresiaServicio.nuevaClienteMembresia(obj);

        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    //Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteMembresiaServicio.borrarClienteMembresia(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
