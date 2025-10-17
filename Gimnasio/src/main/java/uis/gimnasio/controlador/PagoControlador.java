package uis.gimnasio.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uis.gimnasio.modelo.Pago;
import uis.gimnasio.servicio.PagoServicio;

import java.util.List;

@RestController
@RequestMapping("/pago")
public class PagoControlador {

    @Autowired
    PagoServicio pagoServicio;

    //Listar
    @GetMapping("/list")
    public List<Pago> buscarPorId(){
        return pagoServicio.getPago();
    }

    //Buscar Id
    @GetMapping("/list/{id}")
    public Pago buscarPorId(@PathVariable Long id){
        return pagoServicio.buscarPago(id);
    }

    //Agregar un Producto
    @PostMapping("/")
    public ResponseEntity<Pago> agregar(@RequestBody Pago pago) {
        Pago obj = pagoServicio.nuevoPago(pago);
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    //Editar
    @PutMapping("/")
    public ResponseEntity<Pago> editar(@RequestBody Pago pago) {
        Pago obj = pagoServicio.buscarPago(pago.getId());
        if(obj != null){
            obj.setFechaPago(pago.getFechaPago());
            obj.setMonto(pago.getMonto());
            obj.setMetodoPago(pago.getMetodoPago());
            pagoServicio.nuevoPago(obj);
        }else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Pago> eliminar(@PathVariable Long id) {
        Pago obj = pagoServicio.buscarPago(id);
        if(obj != null) {
            pagoServicio.borrarPago(id);
        }else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
}
