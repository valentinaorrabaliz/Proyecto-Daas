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
@Table(name = "cuentas_corrientes")
public class CuentaCorriente extends CuentaBancaria {

    /**
     * Margen asignado a la cuenta corriente.
     */
    @Column(name = "margen", nullable = false, precision = 15, scale = 2)
    private BigDecimal margen;

    /**
     * Comision asociada a la cuenta corriente.
     */
    @Column(name = "costo", nullable = false, precision = 15, scale = 2)
    private BigDecimal costo;
}