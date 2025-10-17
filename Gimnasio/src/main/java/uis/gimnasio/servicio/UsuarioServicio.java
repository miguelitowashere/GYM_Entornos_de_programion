package uis.gimnasio.servicio;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.gimnasio.modelo.Usuario;
import uis.gimnasio.repositorio.UsuarioRepositorio;

import java.util.List;

@Service
@Transactional
public class UsuarioServicio implements IUsuarioServicio {

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @Override
    public List<Usuario> getUsuario() {
        //Listar a todos los usuarios
        return usuarioRepositorio.findAll();
    }

    @Override
    public Usuario nuevoUsuario(Usuario usuario) {
        return usuarioRepositorio.save(usuario);
    }

    @Override
    public Usuario buscarUsuario(Long id) {
        //Buscar usuario por el ID
        Usuario usuario = null;
        usuario= usuarioRepositorio.findById(id).orElse(null);
        if(usuario == null){
            System.out.println("Usuario no encontrado");
        }
        return usuario;
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        Usuario usuario = usuarioRepositorio.findByEmail(email);
        if(usuario == null){
            System.out.println("Usuario no encontrado");
        }
        return usuario;
    }

    @Override
    public int borrarUsuario(Long id) {
        //Borrar
        usuarioRepositorio.deleteById(id);
        return 1;
        //retornarnos el 1 para saber que si borro

    }
}
