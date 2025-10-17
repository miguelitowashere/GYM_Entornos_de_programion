package uis.gimnasio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement; // <--- ¡IMPORTACIÓN CRÍTICA!

@SpringBootApplication
@EnableTransactionManagement // <--- ¡ESTA LÍNEA HACE QUE @Transactional FUNCIONE!
public class GimnasioApplication {

    public static void main(String[] args) {
        SpringApplication.run(GimnasioApplication.class, args);
    }

}