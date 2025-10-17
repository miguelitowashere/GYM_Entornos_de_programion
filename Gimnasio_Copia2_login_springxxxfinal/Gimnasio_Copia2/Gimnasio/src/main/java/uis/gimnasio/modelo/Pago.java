package uis.gimnasio.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = Pago.TABLA_NAME)
public class Pago {

    public static final String TABLA_NAME = "pago";

    // ============================================
    // OPCIÓN 1: Usar 'id' como nombre del campo
    // ============================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long id;  // Lombok genera getId() y setId()

    // Relación directa con ClienteMembresia (RECOMENDADO)
    @ManyToOne
    @JoinColumn(name = "id_cliente_membresia", nullable = false)
    private ClienteMembresia clienteMembresia;  // Lombok genera getClienteMembresia() y setClienteMembresia()

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;
}