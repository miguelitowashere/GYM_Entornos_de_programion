package uis.gimnasio.controlador;

import uis.gimnasio.modelo.Pago;
import uis.gimnasio.modelo.Usuario;
import uis.gimnasio.service.IPagoServicio;
import uis.gimnasio.service.UsuarioServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gimnasio")
@CrossOrigin(origins = "*")
public class PagoControlador {

    @Autowired
    private IPagoServicio pagoServicio;

    @Autowired
    private UsuarioServicioImpl usuarioServicio;

    // ============================================
    // ADMIN Y ENTRENADOR - Listar todos los pagos
    // ============================================
    @GetMapping("/pagos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<?> listarPagos() {
        try {
            List<Pago> pagos = pagoServicio.listarPagos();
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al listar pagos: " + e.getMessage()));
        }
    }

    // ============================================
    // CLIENTE - Ver solo sus propios pagos
    // ============================================
    @GetMapping("/mis-pagos")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> obtenerMisPagos(Authentication authentication) {
        try {
            String email = authentication.getName();
            Usuario usuario = usuarioServicio.buscarUsuarioPorEmail(email);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            // Filtrar pagos del usuario
            List<Pago> todosPagos = pagoServicio.listarPagos();
            List<Pago> misPagos = todosPagos.stream()
                    .filter(p -> p.getClienteMembresia() != null &&
                            p.getClienteMembresia().getUsuario().getId().equals(usuario.getId()))
                    .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(misPagos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener pagos: " + e.getMessage()));
        }
    }

    // ============================================
    // ADMIN Y ENTRENADOR - Buscar pago por ID
    // ============================================
    @GetMapping("/pagos/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<?> buscarPagoPorId(@PathVariable Long id) {
        try {
            Pago pago = pagoServicio.buscarPago(id);
            if (pago != null) {
                return ResponseEntity.ok(pago);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Pago no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al buscar pago: " + e.getMessage()));
        }
    }

    // ============================================
    // SOLO ADMIN - Agregar pago
    // ============================================
    @PostMapping("/pago")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> agregarPago(@RequestBody Pago pago) {
        try {
            Pago nuevoPago = pagoServicio.guardarPago(pago);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPago);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear pago: " + e.getMessage()));
        }
    }

    // ============================================
    // SOLO ADMIN - Editar pago
    // ============================================
    @PutMapping("/pago")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> editarPago(@RequestBody Pago pago) {
        try {
            // Usar getId() en lugar de getId_pago()
            Pago pagoExistente = pagoServicio.buscarPago(pago.getId());

            if (pagoExistente != null) {
                // Usar setClienteMembresia() en lugar de setIdClienteMembresia()
                pagoExistente.setClienteMembresia(pago.getClienteMembresia());
                pagoExistente.setFechaPago(pago.getFechaPago());
                pagoExistente.setMonto(pago.getMonto());
                pagoExistente.setMetodoPago(pago.getMetodoPago());

                Pago pagoActualizado = pagoServicio.guardarPago(pagoExistente);
                return ResponseEntity.ok(pagoActualizado);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Pago no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar pago: " + e.getMessage()));
        }
    }

    // ============================================
    // SOLO ADMIN - Eliminar pago
    // ============================================
    @DeleteMapping("/pago/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> eliminarPago(@PathVariable Long id) {
        try {
            Pago pagoExistente = pagoServicio.buscarPago(id);

            if (pagoExistente != null) {
                pagoServicio.eliminarPago(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Pago no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar pago: " + e.getMessage()));
        }
    }
}