package ticketbeat.modelo;

import ticketbeat.enums.TipoBoleto;
import ticketbeat.enums.EstadoBoleto;

public class Boleto {
    private int idBoleto;
    private TipoBoleto tipo;
    private double precio;
    private EstadoBoleto estado;

    public void reservar() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void vender() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void cancelar() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void liberar() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
