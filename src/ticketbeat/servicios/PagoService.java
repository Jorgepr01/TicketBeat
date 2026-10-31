package ticketbeat.servicios;

import java.util.Map;
import java.util.Objects;
import ticketbeat.modelo.Compra;
import ticketbeat.modelo.Pago;
import ticketbeat.modelo.Reembolso;
import ticketbeat.notificaciones.Notificador;
import ticketbeat.enums.EstadoCompra;
import ticketbeat.enums.EstadoPago;
import ticketbeat.enums.MetodoPago;
import ticketbeat.pagos.ProcesadorPago;

public class PagoService {
    private final Map<MetodoPago, ProcesadorPago> procesadores;
    private final Notificador notificador;

    public PagoService(Map<MetodoPago, ProcesadorPago> procesadores, Notificador notificador) {
        this.procesadores = Map.copyOf(procesadores);
        this.notificador = Objects.requireNonNull(notificador);
    }

    public Pago pagar(Compra compra, MetodoPago metodo) {
        Objects.requireNonNull(compra);
        Objects.requireNonNull(metodo);
        synchronized (compra) {
            if (compra.getEstado() != EstadoCompra.PENDIENTE || !compra.getReserva().estaVigente()) {
                throw new IllegalStateException("La compra no esta pendiente o la reserva ya vencio");
            }
            ProcesadorPago procesador = procesadores.get(metodo);
            if (procesador == null) throw new IllegalArgumentException("Metodo de pago no configurado");
            Pago pago = Objects.requireNonNull(procesador.procesar(compra));
            if (pago.getEstado() == EstadoPago.PENDIENTE) {
                throw new IllegalStateException("La pasarela no devolvio un resultado definitivo");
            }
            compra.registrarPago(pago);
            if (pago.getEstado() == EstadoPago.APROBADO) {
                try {
                    compra.confirmar();
                } catch (IllegalStateException ex) {
                    new Reembolso(pago, pago.getMonto()).procesar();
                    compra.cancelar();
                    throw new IllegalStateException("La reserva ya no se puede confirmar; se simulo el reembolso", ex);
                }
                notificar("Compra " + compra.getIdCompra() + " confirmada. Total: $" + compra.getTotal(), compra);
            } else {
                notificar("Pago rechazado. Puede reintentar mientras la reserva siga vigente.", compra);
            }
            return pago;
        }
    }

    private void notificar(String mensaje, Compra compra) {
        try {
            notificador.enviar(mensaje, compra.getUsuario().getCorreo());
        } catch (RuntimeException ex) {
            System.err.println("No se pudo enviar la notificacion de la compra " + compra.getIdCompra());
        }
    }
}
