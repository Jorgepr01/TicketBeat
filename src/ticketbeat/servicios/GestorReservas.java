package ticketbeat.servicios;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import ticketbeat.modelo.Reserva;

public class GestorReservas implements AutoCloseable {
    private final List<Reserva> reservas;
    private final ScheduledExecutorService reloj;

    public GestorReservas(List<Reserva> reservas) {
        this.reservas = Objects.requireNonNull(reservas);
        reloj = Executors.newSingleThreadScheduledExecutor(tarea -> {
            Thread hilo = new Thread(tarea, "vencimiento-reservas");
            hilo.setDaemon(true);
            return hilo;
        });
        reloj.scheduleAtFixedRate(this::verificarVencimientos, 1, 1, TimeUnit.SECONDS);
    }

    public void verificarVencimientos() {
        for (Reserva reserva : reservas) vencerReserva(reserva);
    }

    public void vencerReserva(Reserva reserva) { Objects.requireNonNull(reserva).vencer(); }
    @Override
    public void close() { reloj.shutdownNow(); }
}
