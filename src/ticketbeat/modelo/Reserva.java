package ticketbeat.modelo;

import java.time.LocalDateTime;
import java.util.List;
import ticketbeat.enums.EstadoReserva;

public class Reserva {
    private int idReserva;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
    private EstadoReserva estado;
    private Usuario usuario;
    private List<Boleto> boletos;

    public long calcularTiempoRestante() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public boolean estaVigente() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void cancelar() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void confirmarPago() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void vencer() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
