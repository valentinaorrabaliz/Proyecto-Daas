package ar.edu.unju.fi.arquitecturas.tp2banco.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Clase base abstracta que proporciona funcionalidad de auditoría a las entidades del sistema.
 *
 * Permite registrar de manera automática la fecha y hora exacta en que un registro
 * es creado y modificado por última vez en la base de datos MySQL, evitando la duplicación
 * de código en cada entidad del modelo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    /**
     * Almacena la fecha y hora exacta en que el registro fue insertado por primera vez.
     *
     * @CreatedDate: Le indica a Spring Data JPA que debe asignar el timestamp actual al crear la entidad.
     * @Column: Mapea el atributo con la columna 'fecha_creacion' en la base de datos.
     *          - nullable = false: Garantiza que el campo nunca sea nulo.
     *          - updatable = false: Impide que esta fecha sea sobreescrita durante futuras actualizaciones del registro.
     */
    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Almacena la fecha y hora de la última modificación realizada sobre el registro.
     *
     * @LastModifiedDate: Le indica a Spring Data JPA que debe actualizar este timestamp
     *                    automáticamente cada vez que el registro sufra alguna modificación.
     * @Column: Mapea el atributo con la columna 'fecha_modificacion' en la base de datos.
     */
    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}