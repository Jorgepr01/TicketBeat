package ticketbeat.modelo;

import java.time.LocalDateTime;
import ticketbeat.enums.EstadoIncidente;

public class Incidente {
    private int idIncidente;
    private String descripcion;
    private LocalDateTime fecha;
    private EstadoIncidente estado;
    private Usuario usuario;

    public void registrar() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void escalar() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void resolver() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
