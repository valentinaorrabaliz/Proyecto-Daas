package ar.edu.unju.fi.arquitecturas.tp2banco.excepcion;

public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}