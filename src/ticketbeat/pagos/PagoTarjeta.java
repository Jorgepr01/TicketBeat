package ticketbeat.pagos;

import ticketbeat.modelo.Compra;
import ticketbeat.modelo.Pago;

public class PagoTarjeta implements ProcesadorPago {
    @Override
    public Pago procesar(Compra compra) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
