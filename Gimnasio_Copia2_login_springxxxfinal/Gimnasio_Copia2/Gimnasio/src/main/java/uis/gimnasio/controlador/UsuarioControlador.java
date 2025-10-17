package uis.gimnasio.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uis.gimnasio.modelo.Usuario;
import uis.gimnasio.service.UsuarioServicioImpl;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "*")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicioImpl usuarioServicio;

    // ============================================
    // SOLO ADMINISTRADOR - Listar todos los usuarios
    // ============================================
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioServicio.getUsuario();
        return ResponseEntity.ok(usuarios);
    }

    // ============================================
    // SOLO ADMINISTRADOR - Buscar usuario por ID
    // ============================================
    @GetMapping("/list/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioServicio.buscarUsuario(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado con ID: " + id));
        }
    }

    // ============================================
    // USUARIO AUTENTICADO - Ver su propio perfil
    // ============================================
    @GetMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtenerPerfil(Authentication authentication) {
        try {
            String email = authentication.getName();
            Usuario usuario = usuarioServicio.buscarUsuarioPorEmail(email);

            if (usuario != null) {
                // No enviar password al frontend
                usuario.setPassword(null);
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener perfil: " + e.getMessage()));
        }
    }

    // ============================================
    // USUARIO AUTENTICADO - Actualizar su propio perfil
    // ============================================
    @PutMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> actualizarPerfil(
            @RequestBody Usuario usuario,
            Authentication authentication) {
        try {
            String emailAutenticado = authentication.getName();
            Usuario usuarioExistente = usuarioServicio.buscarUsuarioPorEmail(emailAutenticado);

            if (usuarioExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            // Solo permitir actualizar ciertos campos
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setApellido(usuario.getApellido());
            usuarioExistente.setTelefono(usuario.getTelefono());

            // Solo actualizar password si se proporciona
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuarioExistente.setPassword(usuario.getPassword());
            }

            Usuario usuarioActualizado = usuarioServicio.nuevoUsuario(usuarioExistente);
            usuarioActualizado.setPassword(null); // No enviar password

            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar perfil: " + e.getMessage()));
        }
    }

    // ============================================
    // SOLO ADMINISTRADOR - Buscar por email
    // ============================================
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email) {
        Usuario usuario = usuarioServicio.buscarUsuarioPorEmail(email);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado con email: " + email));
        }
    }

    // ============================================
    // SOLO ADMINISTRADOR - Agregar usuario
    // ============================================
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> agregar(@RequestBody Usuario usuario) {
        try {
            Usuario existente = usuarioServicio.buscarUsuarioPorEmail(usuario.getEmail());
            if (existente != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El email ya está registrado"));
            }

            Usuario nuevoUsuario = usuarioServicio.nuevoUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear usuario: " + e.getMessage()));
        }
    }

    // ============================================
    // SOLO ADMINISTRADOR - Editar cualquier usuario
    // ============================================
    @PutMapping("/")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> editar(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioExistente = usuarioServicio.buscarUsuario(usuario.getId());
            if (usuarioExistente != null) {
                usuarioExistente.setNombre(usuario.getNombre());
                usuarioExistente.setApellido(usuario.getApellido());
                usuarioExistente.setEmail(usuario.getEmail());
                usuarioExistente.setTelefono(usuario.getTelefono());
                usuarioExistente.setRol(usuario.getRol());

                if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                    usuarioExistente.setPassword(usuario.getPassword());
                }

                Usuario usuarioActualizado = usuarioServicio.nuevoUsuario(usuarioExistente);
                return ResponseEntity.ok(usuarioActualizado);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado con ID: " + usuario.getId()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar usuario: " + e.getMessage()));
        }
    }

    // ============================================
    // SOLO ADMINISTRADOR - Eliminar usuario
    // ============================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioServicio.buscarUsuario(id);
            if (usuario != null) {
                usuarioServicio.borrarUsuario(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado con ID: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar usuario: " + e.getMessage()));
        }
    }
}