package uis.gimnasio.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.gimnasio.modelo.Usuario;
import uis.gimnasio.servicio.UsuarioServicio;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "*") // habilita peticiones desde el frontend
public class UsuarioControlador {

    //Atributos
    @Autowired
    UsuarioServicio usuarioServicio;

    //Listar los usuarios
    @GetMapping("/list")
    public List<Usuario> cargarUsuario(){
        return usuarioServicio.getUsuario();
    }

    //Buscar por Id
    @GetMapping("/list/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return usuarioServicio.buscarUsuario(id);
    }

    // Buscar por email
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        Usuario usuario = usuarioServicio.buscarPorEmail(email);
        if (usuario != null) {
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Agregar un Usuario
    @PostMapping("/")
    public ResponseEntity<Usuario> agregar(@RequestBody Usuario usuario) {
        Usuario obj = usuarioServicio.nuevoUsuario(usuario);
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    //Editar un usuario
    @PutMapping("/")
    public ResponseEntity<Usuario> editar(@RequestBody Usuario usuario){
        Usuario obj = usuarioServicio.buscarUsuario(usuario.getId());
        if(obj != null){
            obj.setNombre(usuario.getNombre());
            obj.setApellido(usuario.getApellido());
            obj.setEmail(usuario.getEmail());
            obj.setPassword(usuario.getPassword());
            obj.setTelefono(usuario.getTelefono());
            usuarioServicio.nuevoUsuario(obj);

        }else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Usuario> eliminar(@PathVariable Long id) {
        Usuario obj = usuarioServicio.buscarUsuario(id);
        if(obj != null) {
            usuarioServicio.borrarUsuario(id);
        }else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
}
