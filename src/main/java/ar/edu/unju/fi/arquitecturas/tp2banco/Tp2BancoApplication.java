package ar.edu.unju.fi.arquitecturas.tp2banco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // <--- Habilita el rastreo de auditoría en todo el proyecto

public class Tp2BancoApplication {

    public static void main(String[] args) {
        SpringApplication.run(Tp2BancoApplication.class, args);
    }

}