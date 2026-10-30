package ticketbeat.pagos;

import ticketbeat.modelo.Compra;
import ticketbeat.modelo.Pago;

public class PagoTransferencia implements ProcesadorPago {
    @Override
    public Pago procesar(Compra compra) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
