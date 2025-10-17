package uis.gimnasio.service;

import uis.gimnasio.modelo.Usuario;
import java.util.List;

public interface UsuarioServicio {
    // Métodos CRUD
    public List<Usuario> getUsuario();
    public Usuario buscarUsuario(Long id);
    public Usuario nuevoUsuario(Usuario usuario);
    public void borrarUsuario(Long id);

    // Método para la seguridad
    public Usuario buscarUsuarioPorEmail(String email);
}