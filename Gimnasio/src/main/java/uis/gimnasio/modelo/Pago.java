package uis.gimnasio.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name=Pago.TABLA_NAME)
public class Pago {
    public static final String TABLA_NAME = "pago";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_cliente_membresia")
    private ClienteMembresia clienteMembresia;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "monto")
    private Double monto;

    @Enumerated(EnumType.STRING)
    @Column(name="metodo_pago")
    private MetodoPago metodoPago;

}
