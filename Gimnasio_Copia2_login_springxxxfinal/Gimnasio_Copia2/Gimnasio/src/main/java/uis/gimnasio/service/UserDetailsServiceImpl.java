package uis.gimnasio.service;

import uis.gimnasio.modelo.Usuario;
import uis.gimnasio.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
// Ya no necesitamos importar java.util.Optional

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Llamamos a findByEmail que devuelve Usuario directamente (sin Optional)
        Usuario usuario = usuarioRepositorio.findByEmail(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        // 2. Mapeo del Rol
        if (usuario.getRol() == null) {
            throw new UsernameNotFoundException("El usuario no tiene un rol asignado.");
        }

        // Crea el rol con el prefijo ROLE_ y mayúsculas
        String rolSpringSecurity = "ROLE_" + usuario.getRol().name().toUpperCase();

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rolSpringSecurity);

        // 3. Retorno: Usamos getPassword() y el rol corregido
        return new User(
                usuario.getEmail(),
                usuario.getPassword(), // <-- Getter corregido (Lombok: getPassword)
                Collections.singletonList(authority)
        );
    }
}