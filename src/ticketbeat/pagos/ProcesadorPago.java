package ticketbeat.pagos;

import ticketbeat.modelo.Compra;
import ticketbeat.modelo.Pago;

public interface ProcesadorPago {
    Pago procesar(Compra compra);
}
