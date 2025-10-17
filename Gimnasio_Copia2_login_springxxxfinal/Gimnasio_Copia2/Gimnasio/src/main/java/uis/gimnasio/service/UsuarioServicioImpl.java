package uis.gimnasio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uis.gimnasio.modelo.Usuario;
import uis.gimnasio.repositorio.UsuarioRepositorio;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // === Métodos CRUD ===

    @Override
    public List<Usuario> getUsuario() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public Usuario buscarUsuario(Long id) {
        // Asumiendo que findById devuelve Optional
        return usuarioRepositorio.findById(id).orElse(null);
    }

    @Override
    @Transactional // CRÍTICO: Asegura el COMMIT a la base de datos
    public Usuario nuevoUsuario(Usuario usuario) {

        // 1. Hasheo de Contraseña
        String rawPassword = usuario.getPassword();

        // Solo encripta si la contraseña no está vacía y no ha sido hasheada previamente
        if (rawPassword != null && !rawPassword.isEmpty() && !rawPassword.startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(rawPassword));
        }

        // 2. Guardar
        return usuarioRepositorio.save(usuario);
    }

    @Override
    public void borrarUsuario(Long id) {
        usuarioRepositorio.deleteById(id);
    }

    // === Métodos de Búsqueda de Seguridad ===

    @Override
    public Usuario buscarUsuarioPorEmail(String email) {
        // Devuelve Usuario o null (como lo requiere UserDetailsServiceImpl, que maneja el 'null')
        return usuarioRepositorio.findByEmail(email);
    }

    // ... otros métodos del servicio ...
}