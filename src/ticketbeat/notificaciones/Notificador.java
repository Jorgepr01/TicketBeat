package ticketbeat.notificaciones;

public interface Notificador {
    void enviar(String mensaje, String destinatario);
}
