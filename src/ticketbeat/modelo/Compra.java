package ticketbeat.modelo;

import java.time.LocalDateTime;
import java.util.List;
import ticketbeat.enums.EstadoCompra;

public class Compra {
    private int idCompra;
    private LocalDateTime fecha;
    private double total;
    private EstadoCompra estado;
    private Usuario usuario;
    private List<Boleto> boletos;
    private List<Pago> pagos;

    public double calcularTotal() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void confirmar() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void cancelar() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
