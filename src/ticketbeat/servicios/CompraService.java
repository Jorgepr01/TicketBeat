package ticketbeat.servicios;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import ticketbeat.modelo.Compra;
import ticketbeat.modelo.Reserva;
import ticketbeat.enums.EstadoCompra;

public class CompraService {
    private static final AtomicInteger SIGUIENTE_ID = new AtomicInteger(1);
    private final Map<Reserva, Compra> compras = new HashMap<>();

    public synchronized Compra crearCompra(Reserva reserva) {
        Objects.requireNonNull(reserva);
        if (!reserva.estaVigente()) throw new IllegalStateException("La reserva no esta vigente");
        Compra existente = compras.get(reserva);
        if (existente != null) {
            if (existente.getEstado() != EstadoCompra.PENDIENTE) {
                throw new IllegalStateException("La reserva ya tiene una compra procesada");
            }
            return existente;
        }
        Compra compra = new Compra(SIGUIENTE_ID.getAndIncrement(), reserva);
        compras.put(reserva, compra);
        return compra;
    }

    public void confirmarCompra(Compra compra) { Objects.requireNonNull(compra).confirmar(); }
    public void cancelarCompra(Compra compra) { Objects.requireNonNull(compra).cancelar(); }
}
