package uis.gimnasio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data; // Incluye @Getter, @Setter, @ToString, @EqualsAndHashCode
import lombok.NoArgsConstructor;

/**
 * Clase de entidad que representa a un Usuario en el gimnasio.
 */
@Entity
@Table(name = "usuarios") // Nombre de la tabla en la base de datos
@Data // Proporciona Getters, Setters, toString, equals y hashCode de Lombok
@AllArgsConstructor // Constructor con todos los argumentos
@NoArgsConstructor // 🚨 CRÍTICO: Constructor vacío/sin argumentos (Necesario para Jackson/JPA)
public class Usuario {

    // Clave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    // Campos del usuario
    @Column(length = 50, nullable = false)
    private String nombre;

    @Column(length = 50, nullable = false)
    private String apellido;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Se almacena hasheada

    @Column(length = 20)
    private String telefono;

    // Relación con el Rol (asumiendo que Rol es una enumeración)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    // NOTA: Si usas Lombok (@Data), los Getters y Setters están incluidos,
    // así como el constructor con todos los argumentos (@AllArgsConstructor)
    // y el constructor sin argumentos (@NoArgsConstructor).
}