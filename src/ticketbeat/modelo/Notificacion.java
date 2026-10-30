package ticketbeat.modelo;

import java.time.LocalDateTime;
import ticketbeat.enums.CanalNotificacion;
import ticketbeat.notificaciones.Notificador;

public class Notificacion {
    private int idNotificacion;
    private String mensajes;
    private LocalDateTime fecha;
    private CanalNotificacion canal;
    private Usuario usuario;
    private Notificador notificador;

    public void enviar() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
