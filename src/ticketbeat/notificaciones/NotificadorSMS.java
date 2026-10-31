package ticketbeat.notificaciones;

public class NotificadorSMS implements Notificador {
    @Override
    public void enviar(String mensaje, String destinatario) {
        if (mensaje == null || mensaje.isBlank() || destinatario == null || destinatario.isBlank()) {
            throw new IllegalArgumentException("Faltan datos de la notificacion");
        }
        System.out.println("[SMS SIMULADO] Para " + destinatario + ": " + mensaje);
    }
}
