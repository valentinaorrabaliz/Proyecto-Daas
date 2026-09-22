package ar.edu.unju.fi.arquitecturas.tp2banco.modelo;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoProcesamiento;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "transacciones")
public class Transaccion extends Auditable {

    /**
     * Identificador único global de la transacción en formato UUID.
     * Evita identificadores secuenciales vulnerables a enumeración.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Fecha en la que se realizó la transacción.
     */
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    /**
     * Hora en la que se registró la transacción.
     */
    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    /**
     * Monto de la transaccion.
     */
    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    /**
     * Tipo o categoría de la transacción.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoTransaccion tipo;

    /**
     * Estado del procesamiento de la transacción.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_procesamiento", nullable = false, length = 30)
    private EstadoProcesamiento estadoProcesamiento;
}