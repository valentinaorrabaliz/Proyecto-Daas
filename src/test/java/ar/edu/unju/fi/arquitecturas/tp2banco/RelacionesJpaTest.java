package ar.edu.unju.fi.arquitecturas.tp2banco;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCliente;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoProcesamiento;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Transaccion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RelacionesJpaTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void deberiaPersistirClienteYCuentaBancaria() {
        // 1. Crear Cliente
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setDni("12345678");
        cliente.setCuil("20123456789");
        cliente.setEmail("juan.perez@example.com");
        cliente.setTelefono("3881234567");
        cliente.setEstadoCliente(EstadoCliente.ACTIVO);

        // 2. Crear CajaDeAhorro
        CajaDeAhorro cajaDeAhorro = new CajaDeAhorro();
        cajaDeAhorro.setCbu("0000003100000000000001");
        cajaDeAhorro.setAlias("PRUEBA.JPA.ALIAS");
        cajaDeAhorro.setSaldo(new BigDecimal("50000.00"));
        cajaDeAhorro.setEstado(EstadoCuenta.ACTIVA);
        cajaDeAhorro.setCupoLimite(5);
        cajaDeAhorro.setTasaInteresAnual(new BigDecimal("0.12"));
        cajaDeAhorro.setCliente(cliente);

        // 3. Persistir entidades
        entityManager.persist(cliente);
        entityManager.persist(cajaDeAhorro);
        entityManager.flush();

        UUID clienteId = cliente.getId();
        UUID cuentaId = cajaDeAhorro.getId();

        // 4. Limpiar contexto de persistencia para consultar directamente a la base de datos
        entityManager.clear();

        // 5. Recuperar y verificar
        Cliente clienteRecuperado = entityManager.find(Cliente.class, clienteId);
        CuentaBancaria cuentaRecuperada = entityManager.find(CuentaBancaria.class, cuentaId);

        assertNotNull(clienteRecuperado);
        assertNotNull(cuentaRecuperada);
        assertEquals("12345678", cuentaRecuperada.getCliente().getDni());
    }

    @Test
    @Transactional
    void deberiaPersistirCuentaConTransaccionesEnCascada() {
        // 1. Crear Cliente
        Cliente cliente = new Cliente();
        cliente.setNombre("Maria");
        cliente.setApellido("Gomez");
        cliente.setDni("87654321");
        cliente.setCuil("27876543219");
        cliente.setEmail("maria.gomez@example.com");
        cliente.setTelefono("3889876543");
        cliente.setEstadoCliente(EstadoCliente.ACTIVO);

        entityManager.persist(cliente);

        // 2. Crear Cuenta
        CajaDeAhorro cuenta = new CajaDeAhorro();
        cuenta.setCbu("0000003100000000000002");
        cuenta.setAlias("CUENTA.TRANSACCION");
        cuenta.setSaldo(new BigDecimal("10000.00"));
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setCupoLimite(10);
        cuenta.setTasaInteresAnual(new BigDecimal("0.15"));
        cuenta.setCliente(cliente);

        // 3. Crear Transacción
        Transaccion transaccion = new Transaccion();
        transaccion.setFecha(LocalDate.now());
        transaccion.setHora(LocalTime.now());
        transaccion.setMonto(new BigDecimal("1500.00"));
        transaccion.setTipo(TipoTransaccion.DEPOSITO);
        transaccion.setEstadoProcesamiento(EstadoProcesamiento.COMPLETADA);

        // --- ASOCIACIÓN BIDIRECCIONAL ---
        // Se establece la referencia de ambos lados de la relación
        transaccion.setCuenta(cuenta); // Lado propietario (donde se guarda la clave foránea cuenta_id)
        cuenta.getTransacciones().add(transaccion); // Lado inverso (colección)

        entityManager.persist(cuenta);
        entityManager.flush();

        UUID cuentaId = cuenta.getId();

        entityManager.clear();

        // 4. Verificar recuperación desde BD
        CuentaBancaria cuentaRecuperada = entityManager.find(CuentaBancaria.class, cuentaId);

        assertNotNull(cuentaRecuperada);
        assertEquals(1, cuentaRecuperada.getTransacciones().size());
        assertEquals(new BigDecimal("1500.00"), cuentaRecuperada.getTransacciones().get(0).getMonto());
    }
}