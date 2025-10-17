package uis.gimnasio.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.gimnasio.modelo.Membresia;
import uis.gimnasio.servicio.MembresiaServicio;

import java.util.List;

@RestController
@RequestMapping("/membresia")
public class MembresiaControlador {

    //Atributos
    @Autowired
    MembresiaServicio membresiaServicio;

    //Listar las membresias
    @GetMapping("/list")
    public List<Membresia> cargarMembresia(){
        return membresiaServicio.getMembresia();
    }

    //Buscar por ID
    @GetMapping("/list/{id}")
    public Membresia buscarPorId(@PathVariable Long id){
        return membresiaServicio.buscarMembresia(id);
    }

    //Agregar una membresia
    @PostMapping("/")
    public ResponseEntity<Membresia> agregar(@RequestBody Membresia membresia){
        Membresia obj = membresiaServicio.nuevaMembresia(membresia);
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    //Editar una membresia
    @PutMapping("/")
    public ResponseEntity<Membresia> editar(@RequestBody Membresia membresia){
        Membresia obj = membresiaServicio.buscarMembresia(membresia.getId());
        if(obj != null){
            obj.setNombre(membresia.getNombre());
            obj.setDescripcion(membresia.getDescripcion());
            obj.setPrecio(membresia.getPrecio());
            obj.setDuracionDias(membresia.getDuracionDias());
            membresiaServicio.nuevaMembresia(obj);

        }else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    //Eliminar membresia
    @DeleteMapping("/{id}")
    public ResponseEntity<Membresia> eliminar(@PathVariable Long id){
        Membresia obj = membresiaServicio.buscarMembresia(id);
        if(obj != null){
            membresiaServicio.borrarMembresia(id);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
}
