package ar.edu.unju.fi.arquitecturas.tp2banco.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cajas_de_ahorro")
public class CajaDeAhorro extends CuentaBancaria {

    /**
     * Limite para la caja de ahorro.
     */
    @Column(name = "cupo_limite", nullable = false)
    private Integer cupoLimite;

    /**
     * tasa de interes anual asociado a la caja.
     */
    @Column(name = "tasa_interes_anual", nullable = false, precision = 5, scale = 2)
    private BigDecimal tasaInteresAnual;
}