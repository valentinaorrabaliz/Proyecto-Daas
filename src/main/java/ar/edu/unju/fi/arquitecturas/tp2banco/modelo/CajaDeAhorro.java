package ar.edu.unju.fi.arquitecturas.tp2banco.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

//lombok
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@SuperBuilder // Permite construir esta clase usando también los atributos que hereda del padre (cuenta bancaria)

//anotaciones JPA para la tabla en mysql
@Entity
@Table(name = "caja_de_ahorro")

public class CajaDeAhorro extends CuentaBancaria {

    private Integer cupoLimite;
    private Float tasaInteresAnual;


}
