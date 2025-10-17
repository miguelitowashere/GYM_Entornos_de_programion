package uis.gimnasio.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import uis.gimnasio.modelo.Pago;

public interface PagoRepositorio extends JpaRepository<Pago, Long> {
}
