package cl.rinconsur.retailflow.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada del backend: levanta el servidor web y conecta la base de datos. */
@SpringBootApplication
public class RetailFlowApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetailFlowApiApplication.class, args);
    }
}
