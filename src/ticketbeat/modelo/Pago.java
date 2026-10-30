package ticketbeat.modelo;

import java.time.LocalDateTime;
import java.util.List;
import ticketbeat.enums.EstadoPago;

public class Pago {
    private int idPago;
    private double monto;
    private LocalDateTime fecha;
    private EstadoPago estado;
    private List<Reembolso> reembolsos;

    public void registrarResultado() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
