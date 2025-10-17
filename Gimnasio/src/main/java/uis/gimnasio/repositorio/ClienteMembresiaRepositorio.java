package uis.gimnasio.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import uis.gimnasio.modelo.ClienteMembresia;

public interface ClienteMembresiaRepositorio extends JpaRepository<ClienteMembresia, Long> {
}
