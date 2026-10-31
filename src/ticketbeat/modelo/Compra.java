package ticketbeat.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ticketbeat.enums.EstadoCompra;
import ticketbeat.enums.EstadoPago;

public class Compra {
    private final int idCompra;
    private final LocalDateTime fecha = LocalDateTime.now();
    private double total;
    private EstadoCompra estado = EstadoCompra.PENDIENTE;
    private final Usuario usuario;
    private final List<Boleto> boletos;
    private final List<Pago> pagos = new ArrayList<>();
    private final Reserva reserva;

    public Compra(int idCompra, Reserva reserva) {
        if (idCompra <= 0) throw new IllegalArgumentException("ID de compra invalido");
        this.reserva = Objects.requireNonNull(reserva);
        if (!reserva.estaVigente()) throw new IllegalStateException("La reserva no esta vigente");
        this.idCompra = idCompra;
        usuario = reserva.getUsuario();
        boletos = reserva.getBoletos();
        total = calcularTotal();
        usuario.registrarCompra(this);
    }

    public double calcularTotal() {
        total = Math.round(boletos.stream().mapToDouble(Boleto::getPrecio).sum() * 100.0) / 100.0;
        return total;
    }

    public synchronized void confirmar() {
        if (estado != EstadoCompra.PENDIENTE) throw new IllegalStateException("La compra no esta pendiente");
        if (pagos.stream().noneMatch(p -> p.getEstado() == EstadoPago.APROBADO
                && Math.abs(p.getMonto() - total) < 0.001)) {
            throw new IllegalStateException("No hay un pago aprobado por el total");
        }
        reserva.confirmarPago();
        estado = EstadoCompra.CONFIRMADA;
    }

    public synchronized void cancelar() {
        if (estado != EstadoCompra.PENDIENTE) {
            throw new IllegalStateException("Solo se cancela una compra pendiente; una pagada requiere devolucion");
        }
        if (reserva.estaVigente()) reserva.cancelar();
        estado = EstadoCompra.CANCELADA;
    }

    public synchronized void registrarPago(Pago pago) {
        Objects.requireNonNull(pago);
        if (estado != EstadoCompra.PENDIENTE || Math.abs(pago.getMonto() - total) > 0.001) {
            throw new IllegalStateException("El pago no corresponde a una compra pendiente por este total");
        }
        if (!pagos.contains(pago)) pagos.add(pago);
    }

    public int getIdCompra() { return idCompra; }
    public LocalDateTime getFecha() { return fecha; }
    public double getTotal() { return total; }
    public synchronized EstadoCompra getEstado() { return estado; }
    public Usuario getUsuario() { return usuario; }
    public List<Boleto> getBoletos() { return boletos; }
    public synchronized List<Pago> getPagos() { return List.copyOf(pagos); }
    public Reserva getReserva() { return reserva; }
}
