package ticketbeat.modelo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import ticketbeat.enums.EstadoBoleto;
import ticketbeat.enums.EstadoReserva;

public class Reserva {
    private final int idReserva;
    private final LocalDateTime fechaCreacion;
    private final LocalDateTime fechaExpiracion;
    private EstadoReserva estado = EstadoReserva.ACTIVA;
    private final Usuario usuario;
    private final List<Boleto> boletos;
    private final Presentacion presentacion;

    public Reserva(int idReserva, Usuario usuario, Presentacion presentacion, List<Boleto> boletos,
            LocalDateTime fechaCreacion, LocalDateTime fechaExpiracion) {
        if (idReserva <= 0) throw new IllegalArgumentException("ID de reserva invalido");
        this.idReserva = idReserva;
        this.usuario = Objects.requireNonNull(usuario);
        this.presentacion = Objects.requireNonNull(presentacion);
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion);
        this.fechaExpiracion = Objects.requireNonNull(fechaExpiracion);
        this.boletos = List.copyOf(boletos);
        if (boletos.isEmpty() || !fechaExpiracion.isAfter(fechaCreacion)
                || boletos.stream().distinct().count() != boletos.size()
                || boletos.stream().anyMatch(b -> !presentacion.getBoletos().contains(b)
                    || b.getEstado() != EstadoBoleto.DISPONIBLE)) {
            throw new IllegalArgumentException("Boletos o fechas de reserva invalidos");
        }
        for (Boleto boleto : boletos) boleto.reservar();
    }

    public synchronized long calcularTiempoRestante() {
        return estado == EstadoReserva.ACTIVA
                ? Math.max(0, Duration.between(LocalDateTime.now(), fechaExpiracion).toMillis()) : 0;
    }

    public synchronized boolean estaVigente() {
        vencer();
        return estado == EstadoReserva.ACTIVA;
    }

    public synchronized void cancelar() {
        if (!estaVigente()) throw new IllegalStateException("La reserva no esta activa");
        liberarBoletos();
        estado = EstadoReserva.CANCELADA;
    }

    public synchronized void confirmarPago() {
        if (!estaVigente()) throw new IllegalStateException("La reserva no esta vigente");
        if (boletos.stream().anyMatch(b -> b.getEstado() != EstadoBoleto.RESERVADO)) {
            throw new IllegalStateException("Los boletos ya no estan reservados");
        }
        for (Boleto boleto : boletos) boleto.vender();
        estado = EstadoReserva.CONFIRMADA;
    }

    public synchronized void vencer() {
        if (estado == EstadoReserva.ACTIVA && !LocalDateTime.now().isBefore(fechaExpiracion)) {
            liberarBoletos();
            estado = EstadoReserva.VENCIDA;
        }
    }

    private void liberarBoletos() {
        for (Boleto boleto : boletos) {
            if (boleto.getEstado() == EstadoBoleto.RESERVADO) boleto.liberar();
        }
    }

    public int getIdReserva() { return idReserva; }
    public Usuario getUsuario() { return usuario; }
    public Presentacion getPresentacion() { return presentacion; }
    public List<Boleto> getBoletos() { return boletos; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public synchronized EstadoReserva getEstado() { vencer(); return estado; }
}
