package uis.gimnasio.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uis.gimnasio.modelo.ClienteMembresia;
import uis.gimnasio.modelo.Usuario;
import uis.gimnasio.service.ClienteMembresiaServicio;
import uis.gimnasio.service.UsuarioServicioImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientemembresia")
@CrossOrigin(origins = "*")
public class ClienteMembresiaControlador {

    @Autowired
    ClienteMembresiaServicio clienteMembresiaServicio;

    @Autowired
    UsuarioServicioImpl usuarioServicio;

    // ============================================
    // ADMIN Y ENTRENADOR - Listar todas las membresías
    // ============================================
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<List<ClienteMembresia>> cargarClienteMembresia() {
        List<ClienteMembresia> lista = clienteMembresiaServicio.getClienteMembresia();
        return ResponseEntity.ok(lista);
    }

    // ============================================
    // CLIENTE - Ver solo SUS propias membresías
    // ============================================
    @GetMapping("/mis-membresias")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> obtenerMisMembresias(Authentication authentication) {
        try {
            String email = authentication.getName();
            Usuario usuario = usuarioServicio.buscarUsuarioPorEmail(email);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            // Filtrar membresías del usuario autenticado
            List<ClienteMembresia> todasMembresias = clienteMembresiaServicio.getClienteMembresia();
            List<ClienteMembresia> misMembresias = todasMembresias.stream()
                    .filter(cm -> cm.getUsuario().getId().equals(usuario.getId()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(misMembresias);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener membresías: " + e.getMessage()));
        }
    }

    // ============================================
    // TODOS AUTENTICADOS - Buscar por ID (con validación)
    // ============================================
    @GetMapping("/list/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, Authentication authentication) {
        try {
            ClienteMembresia clienteMembresia = clienteMembresiaServicio.buscarClienteMembresia(id);

            if (clienteMembresia == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Membresía no encontrada"));
            }

            // Si es CLIENTE, solo puede ver su propia membresía
            String email = authentication.getName();
            Usuario usuarioAutenticado = usuarioServicio.buscarUsuarioPorEmail(email);

            if (usuarioAutenticado.getRol().name().equals("CLIENTE")) {
                if (!clienteMembresia.getUsuario().getId().equals(usuarioAutenticado.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "No tienes permiso para ver esta membresía"));
                }
            }

            return ResponseEntity.ok(clienteMembresia);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al buscar membresía: " + e.getMessage()));
        }
    }

    // ============================================
    // SOLO ADMIN - Agregar membresía
    // ============================================
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> agregar(@RequestBody ClienteMembresia clienteMembresia) {
        try {
            ClienteMembresia obj = clienteMembresiaServicio.nuevaClienteMembresia(clienteMembresia);
            return ResponseEntity.status(HttpStatus.CREATED).body(obj);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear membresía: " + e.getMessage()));
        }
    }

    // ============================================
    // SOLO ADMIN - Editar membresía
    // ============================================
    @PutMapping("/")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> editar(@RequestBody ClienteMembresia clienteMembresia) {
        try {
            ClienteMembresia obj = clienteMembresiaServicio.buscarClienteMembresia(clienteMembresia.getId());

            if (obj != null) {
                obj.setUsuario(clienteMembresia.getUsuario());
                obj.setMembresia(clienteMembresia.getMembresia());
                obj.setFechaInicio(clienteMembresia.getFechaInicio());
                obj.setFechaFin(clienteMembresia.getFechaFin());
                obj.setEstado(clienteMembresia.getEstado());

                ClienteMembresia actualizado = clienteMembresiaServicio.nuevaClienteMembresia(obj);
                return ResponseEntity.ok(actualizado);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Membresía no encontrada"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar membresía: " + e.getMessage()));
        }
    }

    // ============================================
    // SOLO ADMIN - Eliminar membresía
    // ============================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            ClienteMembresia obj = clienteMembresiaServicio.buscarClienteMembresia(id);

            if (obj != null) {
                clienteMembresiaServicio.borrarClienteMembresia(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Membresía no encontrada"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar membresía: " + e.getMessage()));
        }
    }
}