package uis.gimnasio.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import uis.gimnasio.modelo.Membresia;

public interface MembresiaRepositorio extends JpaRepository<Membresia,Long> {
}
