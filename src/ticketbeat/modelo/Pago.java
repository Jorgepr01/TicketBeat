package ticketbeat.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import ticketbeat.enums.EstadoPago;

public class Pago {
    private static final AtomicInteger SIGUIENTE_ID = new AtomicInteger(1);
    private final int idPago;
    private final double monto;
    private final LocalDateTime fecha = LocalDateTime.now();
    private EstadoPago estado = EstadoPago.PENDIENTE;
    private final List<Reembolso> reembolsos = new ArrayList<>();

    public Pago(double monto) {
        if (!Double.isFinite(monto) || monto <= 0) throw new IllegalArgumentException("Monto invalido");
        idPago = SIGUIENTE_ID.getAndIncrement();
        this.monto = monto;
    }

    public void registrarResultado() { registrarResultado(EstadoPago.APROBADO); }

    public void registrarResultado(EstadoPago resultado) {
        Objects.requireNonNull(resultado);
        if (estado != EstadoPago.PENDIENTE || resultado == EstadoPago.PENDIENTE) {
            throw new IllegalStateException("Resultado de pago invalido o ya registrado");
        }
        estado = resultado;
    }

    synchronized void registrarReembolso(Reembolso reembolso) {
        if (estado != EstadoPago.APROBADO || reembolso.getPago() != this || reembolsos.contains(reembolso)) {
            throw new IllegalStateException("El pago no admite este reembolso");
        }
        double devuelto = reembolsos.stream().mapToDouble(Reembolso::getMonto).sum();
        if (devuelto + reembolso.getMonto() > monto + 0.001) {
            throw new IllegalStateException("El reembolso supera el monto pagado");
        }
        reembolsos.add(reembolso);
    }

    public int getIdPago() { return idPago; }
    public double getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public EstadoPago getEstado() { return estado; }
    public synchronized List<Reembolso> getReembolsos() { return List.copyOf(reembolsos); }
}
