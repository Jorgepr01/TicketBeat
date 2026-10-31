package ticketbeat.modelo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import ticketbeat.enums.EstadoBoleto;
import ticketbeat.enums.EstadoPresentacion;

public class Presentacion {
    private final int idPresentacion;
    private final LocalDateTime fechaHora;
    private final EstadoPresentacion estado = EstadoPresentacion.PROGRAMADA;
    private final Lugar lugar;
    private final List<Boleto> boletos;
    private final Evento evento;

    public Presentacion(int idPresentacion, LocalDateTime fechaHora, Lugar lugar,
            Evento evento, List<Boleto> boletos) {
        if (idPresentacion <= 0) throw new IllegalArgumentException("ID de presentacion invalido");
        this.idPresentacion = idPresentacion;
        this.fechaHora = Objects.requireNonNull(fechaHora);
        this.lugar = Objects.requireNonNull(lugar);
        this.evento = Objects.requireNonNull(evento);
        this.boletos = List.copyOf(boletos);
        if (boletos.size() > lugar.consultarCapacidad()
                || boletos.stream().map(Boleto::getIdBoleto).distinct().count() != boletos.size()) {
            throw new IllegalArgumentException("Inventario repetido o mayor que la capacidad del lugar");
        }
    }

    public int consultarDisponibilidad() {
        return (int) boletos.stream().filter(b -> b.getEstado() == EstadoBoleto.DISPONIBLE).count();
    }

    public int getIdPresentacion() { return idPresentacion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public EstadoPresentacion getEstado() { return estado; }
    public Lugar getLugar() { return lugar; }
    public Evento getEvento() { return evento; }
    public List<Boleto> getBoletos() { return boletos; }
}
