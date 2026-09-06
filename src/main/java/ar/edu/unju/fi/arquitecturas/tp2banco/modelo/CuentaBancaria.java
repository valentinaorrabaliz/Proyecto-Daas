package ar.edu.unju.fi.arquitecturas.tp2banco.modelo;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCuenta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

//lombok
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder //permite que las clases hijas (CajaDeAhorro, CuentaCorriente) hereden el builder

//anotaciones JPA para la tabla en mysql
@Entity
@Table(name = "cuenta_Bancaria")

// lindica a JPA cómo mapear la herencia en la base de datos (JOINED crea una tabla para la clase padre y una para cada clase hija)
@Inheritance(strategy = InheritanceType.JOINED)


@EntityListeners(AuditingEntityListener.class) // <--- Registra eventos de creación/modificación

public abstract class CuentaBancaria {


    @Id //clave primaria de la tabla en la BD
    @GeneratedValue(strategy = GenerationType.IDENTITY) //MySQL genera el ID automáticamente de forma incremental
    private Long id;


    private Long cbu;
    private String alias;
    private Float saldo;

    @Enumerated(EnumType.STRING)
    private EstadoCuenta estado;


    //CAMPOS DE AUDITORÍA
    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion; //se registra automáticamente al insertar

    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    //se actualiza automáticamente al modificar
    }