package uis.gimnasio.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uis.gimnasio.modelo.Pago; // Debe importar el modelo correcto

@Repository
public interface PagoRepositorio extends JpaRepository<Pago, Long> {
    // Aquí puedes añadir métodos de búsqueda personalizados si los necesitas.
}