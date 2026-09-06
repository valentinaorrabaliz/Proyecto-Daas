package ar.edu.unju.fi.arquitecturas.tp2banco.modelo;


import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoProcesamiento;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

//lombok
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder // Crea un constructor fluido para instanciar el objeto campo por campo. Se usa en clases simples sin herencia.

//anotaciones JPA para la tabla en mysql
@Entity
@Table(name = "transaccion")


@EntityListeners(AuditingEntityListener.class) // <--- Registra eventos de creación/modificación


public class Transaccion {

    @Id // Indica que 'id' es la clave primaria de la tabla en la BD
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Hace que MySQL genere el ID automáticamente de forma incremental
    private Integer id;


    @Temporal(TemporalType.DATE)
    private LocalDate fecha;

    @Temporal(TemporalType.TIME)
    private LocalTime hora;

    private Float monto;



    @Enumerated(EnumType.STRING)
    private TipoTransaccion tipo;

    @Enumerated(EnumType.STRING)
    private EstadoProcesamiento estadoProcesamiento;


    // --- CAMPOS DE AUDITORÍA ---
    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion; // Se registra automáticamente al insertar

    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion; // Se actualiza automáticamente al modificar




}
