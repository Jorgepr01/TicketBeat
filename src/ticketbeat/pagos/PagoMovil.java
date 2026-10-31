package ticketbeat.pagos;

import ticketbeat.modelo.Compra;
import ticketbeat.modelo.Pago;

public class PagoMovil implements ProcesadorPago {
    @Override
    public Pago procesar(Compra compra) {
        Pago pago = new Pago(compra.calcularTotal());
        pago.registrarResultado();
        return pago;
    }
}
