package ticketbeat.modelo;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import ticketbeat.enums.EstadoReembolso;

public class Reembolso {
    private static final AtomicInteger SIGUIENTE_ID = new AtomicInteger(1);
    private final int idReembolso;
    private final double monto;
    private EstadoReembolso estado = EstadoReembolso.PENDIENTE;
    private final Pago pago;

    public Reembolso(Pago pago, double monto) {
        if (!Double.isFinite(monto) || monto <= 0) throw new IllegalArgumentException("Monto invalido");
        this.pago = Objects.requireNonNull(pago);
        this.monto = monto;
        idReembolso = SIGUIENTE_ID.getAndIncrement();
    }

    public synchronized void procesar() {
        if (estado != EstadoReembolso.PENDIENTE) throw new IllegalStateException("Reembolso ya procesado");
        try {
            pago.registrarReembolso(this);
            estado = EstadoReembolso.PROCESADO;
        } catch (IllegalStateException ex) {
            estado = EstadoReembolso.RECHAZADO;
            throw ex;
        }
    }

    public int getIdReembolso() { return idReembolso; }
    public double getMonto() { return monto; }
    public EstadoReembolso getEstado() { return estado; }
    public Pago getPago() { return pago; }
}
