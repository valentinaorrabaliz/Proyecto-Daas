package ar.edu.unju.fi.arquitecturas.tp2banco.excepcion;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}