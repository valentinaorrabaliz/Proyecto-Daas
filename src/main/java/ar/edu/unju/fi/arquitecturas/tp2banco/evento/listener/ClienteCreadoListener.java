package ar.edu.unju.fi.arquitecturas.tp2banco.evento.listener;

import ar.edu.unju.fi.arquitecturas.tp2banco.evento.ClienteCreadoEvent;
import ar.edu.unju.fi.arquitecturas.tp2banco.servicios.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteCreadoListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void manejarClienteCreado(ClienteCreadoEvent event) {
        emailService.enviarEmailActivacion(
                event.getEmail(),
                event.getNombre(),
                event.getTokenActivacion()
        );
    }
}