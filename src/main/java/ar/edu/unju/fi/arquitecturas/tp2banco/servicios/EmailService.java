package ar.edu.unju.fi.arquitecturas.tp2banco.servicios;

public interface EmailService {
    void enviarEmailActivacion(String destinatario, String nombreCliente, String tokenActivacion);
}