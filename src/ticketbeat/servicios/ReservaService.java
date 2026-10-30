package ticketbeat.servicios;

import ticketbeat.modelo.Usuario;
import ticketbeat.modelo.Presentacion;
import ticketbeat.modelo.Reserva;
import ticketbeat.enums.TipoBoleto;

public class ReservaService {

    public Reserva reservar(Usuario usuario, Presentacion presentacion, TipoBoleto tipo, int cantidad) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public boolean verificarDisponibilidad(Presentacion presentacion, TipoBoleto tipo, int cantidad) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void liberarReserva(Reserva reserva) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
