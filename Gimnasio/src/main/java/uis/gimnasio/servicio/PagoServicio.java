package uis.gimnasio.servicio;

import jakarta.transaction.Transactional;
import org.hibernate.sql.ast.tree.expression.Over;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.gimnasio.modelo.Pago;
import uis.gimnasio.repositorio.PagoRepositorio;

import java.util.List;

@Service
@Transactional
public class PagoServicio implements IPagoServicio {

    @Autowired
    PagoRepositorio pagoRepositorio;

    //Listar
    @Override
    public List<Pago> getPago(){
        return pagoRepositorio.findAll();
    }

    @Override
    //Guardar
    public Pago nuevoPago(Pago pago) {
        return pagoRepositorio.save(pago);
    }

    @Override
    //Buscar
    public Pago buscarPago(Long id){
        return pagoRepositorio.findById(id).orElse(null);
    }

    @Override
    // Borrar
    public int borrarPago(Long id){
        pagoRepositorio.deleteById(id);
        return 1; // 1 significa que se borró correctamente
    }

}
