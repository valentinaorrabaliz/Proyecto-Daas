package ar.edu.unju.fi.arquitecturas.tp2banco.modelo;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCuenta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Clase base abstracta para todas las cuentas bancarias del sistema.
 *
 * Implementa estrategia JOINED para herencia en JPA y mantiene una relacion
 * unidireccional `@OneToMany` hacia la entidad Transaccion con borrado en cascada (orphanRemoval).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cuentas_bancarias")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CuentaBancaria extends Auditable {

    /**
     * Identificador unico global para la cuenta bancaria utilizando UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * CBU de la cuenta en Argentina.
     * Numero unico de 22 digitos.
     */
    @Column(name = "cbu", nullable = false, unique = true, length = 22)
    private String cbu;

    /**
     * Alias asignado a la cuenta bancaria.
     */
    @Column(name = "alias", nullable = false, unique = true, length = 40)
    private String alias;

    /**
     * Saldo actual en la cuenta. Se utiliza BigDecimal por precision financiera.
     */
    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    /**
     * Estado operativo de la cuenta bancaria.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCuenta estado;

    /**
     * Cliente titular de la cuenta bancaria.
     * Relacion muchos a uno: Muchas cuentas pertenecen a un único cliente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /**
     * Relacion unidireccional de uno a muchos hacia las transacciones de esta cuenta.
     *
     * @JoinColumn: Agrega la clave foranea 'cuenta_id' directamente en la tabla de transacciones.
     * cascade = CascadeType.ALL: Cualquier operacion de persistencia se extiende a las transacciones.
     * orphanRemoval = true: Si una transaccion se elimina de esta lista, se borra de la BD automaticamente.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private List<Transaccion> transacciones = new ArrayList<>();
}