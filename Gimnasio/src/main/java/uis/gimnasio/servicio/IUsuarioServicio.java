package uis.gimnasio.servicio;

import uis.gimnasio.modelo.Usuario;

import java.util.List;

public interface IUsuarioServicio {

    //Cabecera de los metodos

    //Listar los usuarios
    List<Usuario> getUsuario();

    //Nuevo Usuario
    Usuario nuevoUsuario(Usuario usuario);

    //Buscar un usuario
    Usuario buscarUsuario(Long id);

    //Buscar por Gmail
    Usuario buscarPorEmail(String email);

    //Eliminar
    int borrarUsuario(Long id);
}
