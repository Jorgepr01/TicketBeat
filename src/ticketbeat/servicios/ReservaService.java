package ticketbeat.servicios;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import ticketbeat.modelo.Boleto;
import ticketbeat.modelo.Presentacion;
import ticketbeat.modelo.Reserva;
import ticketbeat.modelo.Usuario;
import ticketbeat.enums.EstadoBoleto;
import ticketbeat.enums.EstadoReserva;
import ticketbeat.enums.EstadoPresentacion;
import ticketbeat.enums.TipoBoleto;

public class ReservaService {
    private static final AtomicInteger SIGUIENTE_ID = new AtomicInteger(1);
    private final List<Reserva> reservas = new CopyOnWriteArrayList<>();
    private final Duration duracion;

    public ReservaService() { this(Duration.ofMinutes(15)); }

    public ReservaService(Duration duracion) {
        if (duracion == null || duracion.isZero() || duracion.isNegative()) {
            throw new IllegalArgumentException("La duracion debe ser positiva");
        }
        this.duracion = duracion;
    }

    public synchronized Reserva reservar(Usuario usuario, Presentacion presentacion, TipoBoleto tipo, int cantidad) {
        Objects.requireNonNull(usuario);
        Objects.requireNonNull(presentacion);
        Objects.requireNonNull(tipo);
        synchronized (presentacion) {
            for (Reserva reserva : reservas) reserva.vencer();
            int acumulado = reservas.stream().filter(r -> r.getUsuario().getIdUsuario() == usuario.getIdUsuario()
                    && r.getPresentacion().getEvento().getIdEvento() == presentacion.getEvento().getIdEvento()
                    && (r.getEstado() == EstadoReserva.ACTIVA || r.getEstado() == EstadoReserva.CONFIRMADA))
                    .mapToInt(r -> r.getBoletos().size()).sum();
            int limite = presentacion.getEvento().getPolitica().getMaxBoletosPorUsuario();
            if (!presentacion.getEvento().getPolitica().validarLimite(cantidad) || cantidad > limite - acumulado) {
                throw new IllegalArgumentException("Se excede el limite de entradas por usuario para este evento");
            }
            if (!verificarDisponibilidad(presentacion, tipo, cantidad)) {
                throw new IllegalStateException("No hay suficientes entradas disponibles o la presentacion no esta habilitada");
            }
            List<Boleto> seleccionados = presentacion.getBoletos().stream()
                    .filter(b -> b.getTipo() == tipo && b.getEstado() == EstadoBoleto.DISPONIBLE)
                    .limit(cantidad).toList();
            LocalDateTime ahora = LocalDateTime.now();
            Reserva reserva = new Reserva(SIGUIENTE_ID.getAndIncrement(), usuario, presentacion,
                    seleccionados, ahora, ahora.plus(duracion));
            reservas.add(reserva);
            return reserva;
        }
    }

    public boolean verificarDisponibilidad(Presentacion presentacion, TipoBoleto tipo, int cantidad) {
        Objects.requireNonNull(presentacion);
        Objects.requireNonNull(tipo);
        synchronized (presentacion) {
            for (Reserva reserva : reservas) reserva.vencer();
            return cantidad > 0 && presentacion.getEstado() != EstadoPresentacion.CANCELADA
                    && presentacion.getFechaHora().isAfter(LocalDateTime.now())
                    && presentacion.getBoletos().stream().filter(b -> b.getTipo() == tipo
                        && b.getEstado() == EstadoBoleto.DISPONIBLE).count() >= cantidad;
        }
    }

    public void liberarReserva(Reserva reserva) { Objects.requireNonNull(reserva).cancelar(); }
    public List<Reserva> getReservas() { return Collections.unmodifiableList(reservas); }
}
