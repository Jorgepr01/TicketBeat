package ticketbeat.modelo;

import java.util.Objects;
import ticketbeat.enums.EstadoBoleto;
import ticketbeat.enums.TipoBoleto;

public class Boleto {
    private final int idBoleto;
    private final TipoBoleto tipo;
    private final double precio;
    private EstadoBoleto estado = EstadoBoleto.DISPONIBLE;

    public Boleto(int idBoleto, TipoBoleto tipo, double precio) {
        if (idBoleto <= 0 || !Double.isFinite(precio) || precio <= 0) {
            throw new IllegalArgumentException("ID o precio del boleto invalidos");
        }
        this.idBoleto = idBoleto;
        this.tipo = Objects.requireNonNull(tipo);
        this.precio = precio;
    }

    public synchronized void reservar() {
        if (estado != EstadoBoleto.DISPONIBLE) throw new IllegalStateException("Boleto no disponible");
        estado = EstadoBoleto.RESERVADO;
    }

    public synchronized void vender() {
        if (estado != EstadoBoleto.RESERVADO) throw new IllegalStateException("El boleto no esta reservado");
        estado = EstadoBoleto.VENDIDO;
    }

    public synchronized void cancelar() { estado = EstadoBoleto.CANCELADO; }

    public synchronized void liberar() {
        if (estado != EstadoBoleto.RESERVADO) throw new IllegalStateException("Solo se libera un boleto reservado");
        estado = EstadoBoleto.DISPONIBLE;
    }

    public int getIdBoleto() { return idBoleto; }
    public String getCodigo() { return "TB-" + idBoleto; }
    public TipoBoleto getTipo() { return tipo; }
    public double getPrecio() { return precio; }
    public synchronized EstadoBoleto getEstado() { return estado; }
}
