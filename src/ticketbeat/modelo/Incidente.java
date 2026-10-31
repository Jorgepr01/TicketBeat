package ticketbeat.modelo;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import ticketbeat.enums.EstadoIncidente;

public class Incidente {
    private static final AtomicInteger SIGUIENTE_ID = new AtomicInteger(1);
    private final int idIncidente;
    private final String descripcion;
    private final LocalDateTime fecha = LocalDateTime.now();
    private EstadoIncidente estado;
    private final Usuario usuario;

    public Incidente(Usuario usuario, String descripcion) {
        if (descripcion == null || descripcion.isBlank()) throw new IllegalArgumentException("Describa el incidente");
        idIncidente = SIGUIENTE_ID.getAndIncrement();
        this.usuario = Objects.requireNonNull(usuario);
        this.descripcion = descripcion;
        registrar();
    }

    public void registrar() {
        if (estado != null) throw new IllegalStateException("El incidente ya esta registrado");
        estado = EstadoIncidente.REGISTRADO;
    }

    public void escalar() {
        if (estado != EstadoIncidente.REGISTRADO) throw new IllegalStateException("El incidente no se puede escalar");
        estado = EstadoIncidente.ESCALADO;
    }

    public void resolver() {
        if (estado == EstadoIncidente.RESUELTO) throw new IllegalStateException("El incidente ya esta resuelto");
        estado = EstadoIncidente.RESUELTO;
    }

    public int getIdIncidente() { return idIncidente; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFecha() { return fecha; }
    public EstadoIncidente getEstado() { return estado; }
    public Usuario getUsuario() { return usuario; }
}
