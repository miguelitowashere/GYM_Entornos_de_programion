package uis.gimnasio.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

@Table
public class Membresia {
    public static final String TABLA_NAME= "membresia";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_membresia")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "duracion_dias")
    private int duracionDias;
}

