package ar.edu.unju.fi.arquitecturas.tp2banco.modelo;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


//lombok
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder // Crea un constructor fluido para instanciar el objeto campo por campo. Se usa en clases simples sin herencia.


//anotaciones JPA para la tabla en mysql
@Entity
@Table(name = "cliente")


@EntityListeners(AuditingEntityListener.class) // <--- Registra eventos de creación/modificación



public class Cliente {

    @Id // Indica que 'id' es la clave primaria de la tabla en la BD
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Hace que MySQL genere el ID automáticamente de forma incremental
    private Long id;


    private String nombre;
    private Long cuil;
    private String email;
    private Integer telefono;
    private String direccion;


    // --- CAMPOS DE AUDITORÍA ---
    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion; // Se registra automáticamente al insertar

    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion; // Se actualiza automáticamente al modificar

}
