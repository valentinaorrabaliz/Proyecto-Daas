package ar.edu.unju.fi.arquitecturas.tp2banco.scheduler;

import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.EstadoProcesamiento;
import ar.edu.unju.fi.arquitecturas.tp2banco.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.tp2banco.modelo.Transaccion;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.ConfiguracionParametroRepository;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.tp2banco.repositorio.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LiquidacionComisionesScheduler {

    private final CuentaBancariaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;
    private final ConfiguracionParametroRepository parametroRepository;

    @Scheduled(cron = "${app.comisiones.cron:0 0 0 1 * ?}")
    @Transactional
    public void ejecutarLiquidacionMensual() {
        // 1. Obtener ambos parámetros de la BDD
        BigDecimal comisionCajaAhorro = obtenerMontoParametro("COMISION_CAJA_AHORRO", new BigDecimal("2000.00"));
        BigDecimal comisionCuentaCorriente = obtenerMontoParametro("COMISION_CUENTA_CORRIENTE", new BigDecimal("5000.00"));

        List<CuentaBancaria> cuentasActivas = cuentaRepository.findByEstado(EstadoCuenta.ACTIVA);

        for (CuentaBancaria cuenta : cuentasActivas) {
            // 2. Evaluar el tipo de cuenta según la subclase con instanceof
            BigDecimal comisionAAplicar = (cuenta instanceof CajaDeAhorro)
                    ? comisionCajaAhorro
                    : comisionCuentaCorriente;

            // 3. Descontar importe
            cuenta.setSaldo(cuenta.getSaldo().subtract(comisionAAplicar));
            cuentaRepository.save(cuenta);

            // 4. Registrar la transacción
            Transaccion debito = new Transaccion();
            debito.setCuenta(cuenta);
            debito.setTipo(TipoTransaccion.DEBITO_COMISION);
            debito.setMonto(comisionAAplicar);
            debito.setFecha(LocalDate.now());
            debito.setHora(LocalTime.now());
            debito.setEstadoProcesamiento(EstadoProcesamiento.COMPLETADA);
            transaccionRepository.save(debito);
        }
    }

    private BigDecimal obtenerMontoParametro(String clave, BigDecimal porDefecto) {
        return parametroRepository.findById(clave)
                .map(p -> new BigDecimal(p.getValor()))
                .orElse(porDefecto);
    }
}