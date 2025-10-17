package uis.gimnasio.servicio;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uis.gimnasio.modelo.Membresia;
import uis.gimnasio.repositorio.MembresiaRepositorio;

import java.util.List;

@Service
@Transactional
public class MembresiaServicio implements IMembresiaServicio{

    @Autowired
    MembresiaRepositorio membresiaRepositorio;

    //Listar todas las membresias
    @Override
    public List<Membresia> getMembresia(){
        return membresiaRepositorio.findAll();
    }
    // Grabar los datos de una nueva membresia
    @Override
    public Membresia nuevaMembresia(Membresia membresia){
        return membresiaRepositorio.save(membresia);
    }

    //Buscar membresia por ID
    public Membresia buscarMembresia(Long id){
        Membresia membresia = null;
        membresia = membresiaRepositorio.findById(id).orElse(null);
        if(membresia == null){
            System.out.println("Membresia no encontrado");
        }
        return membresia;
    }

    //Borrar membresia
    public int borrarMembresia(Long id){
        membresiaRepositorio.deleteById(id);
        return 1;
        // Retorna el 1 para decirnos que si borro
    }

}
