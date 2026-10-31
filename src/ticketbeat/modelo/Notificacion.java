package ticketbeat.modelo;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import ticketbeat.enums.CanalNotificacion;
import ticketbeat.notificaciones.Notificador;

public class Notificacion {
    private static final AtomicInteger SIGUIENTE_ID = new AtomicInteger(1);
    private final int idNotificacion;
    private final String mensajes;
    private final LocalDateTime fecha = LocalDateTime.now();
    private final CanalNotificacion canal;
    private final Usuario usuario;
    private final Notificador notificador;

    public Notificacion(String mensaje, Usuario usuario, CanalNotificacion canal, Notificador notificador) {
        if (mensaje == null || mensaje.isBlank()) throw new IllegalArgumentException("Mensaje vacio");
        idNotificacion = SIGUIENTE_ID.getAndIncrement();
        mensajes = mensaje;
        this.usuario = Objects.requireNonNull(usuario);
        this.canal = Objects.requireNonNull(canal);
        this.notificador = Objects.requireNonNull(notificador);
    }

    public void enviar() { notificador.enviar(mensajes, usuario.getCorreo()); }
    public int getIdNotificacion() { return idNotificacion; }
    public String getMensajes() { return mensajes; }
    public LocalDateTime getFecha() { return fecha; }
    public CanalNotificacion getCanal() { return canal; }
}
