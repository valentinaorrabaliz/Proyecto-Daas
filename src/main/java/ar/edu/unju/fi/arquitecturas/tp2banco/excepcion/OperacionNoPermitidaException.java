package ar.edu.unju.fi.arquitecturas.tp2banco.excepcion;

public class OperacionNoPermitidaException extends RuntimeException {
    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}