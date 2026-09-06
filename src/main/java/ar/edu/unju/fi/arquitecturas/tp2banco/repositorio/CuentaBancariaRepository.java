package ar.edu.unju.fi.arquitecturas.tp2banco.repositorio;

import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaBancariaRepository extends JpaRepository<CuentaBancaria, Long> {

    //buscar una cuenta bancaria mediante su CBU
    Optional<CuentaBancaria> findByCbu(String cbu);

    //obtener todas las cuentas según su estado actual
    List<CuentaBancaria> findByEstado(EstadoCuenta estado);
}