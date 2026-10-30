package ticketbeat.modelo;

import java.time.LocalDateTime;
import java.util.List;
import ticketbeat.enums.EstadoPresentacion;

public class Presentacion {
    private int idPresentacion;
    private LocalDateTime fechaHora;
    private EstadoPresentacion estado;
    private Lugar lugar;
    private List<Boleto> boletos;

    public int consultarDisponibilidad() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
