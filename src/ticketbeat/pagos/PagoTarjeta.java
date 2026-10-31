package ticketbeat.pagos;

import ticketbeat.modelo.Compra;
import ticketbeat.modelo.Pago;

public class PagoTarjeta implements ProcesadorPago {
    @Override
    public Pago procesar(Compra compra) {
        Pago pago = new Pago(compra.calcularTotal());
        pago.registrarResultado();
        return pago;
    }
}
