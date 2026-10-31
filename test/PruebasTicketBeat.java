import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import ticketbeat.enums.*;
import ticketbeat.modelo.*;
import ticketbeat.notificaciones.*;
import ticketbeat.pagos.*;
import ticketbeat.servicios.*;

public class PruebasTicketBeat {
    private static int verificaciones;

    private static class Escenario {
        final Usuario usuario = new Usuario(1, "Martin", "martin@example.com", "demo");
        final Usuario otro = new Usuario(2, "Ricardo", "ricardo@example.com", "demo");
        final Evento evento;
        final Presentacion presentacion;
        final ReservaService reservas = new ReservaService();
        final CompraService compras = new CompraService();
        final ConsultaBoletosService consulta = new ConsultaBoletosService();

        Escenario(int cantidad, int limite) {
            evento = new Evento(1, "Festival del Puerto", "Concierto", "Rock",
                    List.of(new Artista(1, "Banda", "Rock")),
                    new PoliticaOrganizador("Permitida", "Permitido", limite, true, true));
            List<Boleto> boletos = new ArrayList<>();
            for (int i = 1; i <= cantidad; i++) boletos.add(new Boleto(i, TipoBoleto.GENERAL, 25));
            presentacion = new Presentacion(1, LocalDateTime.now().plusDays(30),
                    new Lugar(1, "Teatro", "Av. Principal", "Guayaquil", 100), evento, boletos);
            evento.agregarPresentacion(presentacion);
        }

        PagoService pagos(ProcesadorPago procesador) {
            return new PagoService(Map.of(MetodoPago.TARJETA, procesador), (mensaje, destino) -> { });
        }
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
        verificaciones++;
    }

    private static void rechaza(Class<? extends RuntimeException> tipo, Runnable accion) {
        try {
            accion.run();
        } catch (RuntimeException ex) {
            verificar(tipo.isInstance(ex), "Se recibio una excepcion distinta: " + ex);
            return;
        }
        throw new AssertionError("Se esperaba " + tipo.getSimpleName());
    }

    private static void buscarEventos() {
        Escenario e = new Escenario(3, 4);
        BuscadorEventos buscador = new BuscadorEventos(List.of(e.evento));
        verificar(buscador.buscarPorNombre("  FESTIVAL ").size() == 1, "Busqueda por nombre");
        verificar(buscador.buscarPorArtista(1).size() == 1, "Busqueda por artista");
        verificar(buscador.buscarPorLugar(1).size() == 1, "Busqueda por lugar");
        verificar(buscador.buscarPorGenero("rock").size() == 1, "Busqueda por genero");
        verificar(buscador.buscarPorFecha(e.presentacion.getFechaHora().toLocalDate()).size() == 1, "Busqueda por fecha");
        verificar(buscador.buscarPorNombre("inexistente").isEmpty(), "Sin resultados");
        rechaza(IllegalArgumentException.class, () -> buscador.buscarPorNombre(" "));
    }

    private static void compraCompleta() {
        Escenario e = new Escenario(3, 4);
        verificar(e.consulta.consultarPorUsuario(e.usuario).isEmpty(), "Usuario sin compras");
        Reserva reserva = e.reservas.reservar(e.usuario, e.presentacion, TipoBoleto.GENERAL, 2);
        verificar(e.presentacion.consultarDisponibilidad() == 1, "Los reservados dejan de estar disponibles");
        Compra compra = e.compras.crearCompra(reserva);
        verificar(e.compras.crearCompra(reserva) == compra, "No se duplica la compra de una reserva");
        verificar(e.consulta.consultarPorUsuario(e.usuario).isEmpty(), "Una reserva no es una compra pagada");
        rechaza(IllegalStateException.class, compra::confirmar);
        Pago pago = e.pagos(new PagoTarjeta()).pagar(compra, MetodoPago.TARJETA);
        verificar(pago.getMonto() == 50 && pago.getEstado() == EstadoPago.APROBADO, "Monto y pago aprobado");
        verificar(compra.getEstado() == EstadoCompra.CONFIRMADA, "Compra confirmada");
        verificar(reserva.getEstado() == EstadoReserva.CONFIRMADA, "Reserva confirmada");
        verificar(e.consulta.consultarPorUsuario(e.usuario).size() == 2, "El comprador ve sus dos boletos");
        verificar(e.consulta.consultarPorUsuario(e.otro).isEmpty(), "Otro usuario no ve los boletos");
        rechaza(IllegalStateException.class, () -> e.pagos(new PagoTarjeta()).pagar(compra, MetodoPago.TARJETA));
    }

    private static void limitesYCupos() {
        Escenario e = new Escenario(3, 2);
        e.reservas.reservar(e.usuario, e.presentacion, TipoBoleto.GENERAL, 1);
        rechaza(IllegalArgumentException.class, () -> e.reservas.reservar(e.usuario, e.presentacion, TipoBoleto.GENERAL, 2));
        rechaza(IllegalArgumentException.class, () -> e.reservas.reservar(e.usuario, e.presentacion, TipoBoleto.GENERAL, 0));
        rechaza(IllegalStateException.class, () -> e.reservas.reservar(e.otro, e.presentacion, TipoBoleto.VIP, 1));
        e.reservas.reservar(e.otro, e.presentacion, TipoBoleto.GENERAL, 2);
        verificar(e.presentacion.consultarDisponibilidad() == 0, "Inventario agotado");
    }

    private static void cancelarReservaYCompra() {
        Escenario e = new Escenario(2, 2);
        Reserva reserva = e.reservas.reservar(e.usuario, e.presentacion, TipoBoleto.GENERAL, 2);
        Compra compra = e.compras.crearCompra(reserva);
        e.compras.cancelarCompra(compra);
        verificar(e.presentacion.consultarDisponibilidad() == 2, "Cancelar libera los boletos");
        verificar(compra.getEstado() == EstadoCompra.CANCELADA, "Compra cancelada");
        rechaza(IllegalStateException.class, () -> e.pagos(new PagoTarjeta()).pagar(compra, MetodoPago.TARJETA));
    }

    private static void pagoRechazadoYReintento() {
        Escenario e = new Escenario(1, 2);
        Compra compra = e.compras.crearCompra(e.reservas.reservar(e.usuario, e.presentacion, TipoBoleto.GENERAL, 1));
        ProcesadorPago rechazado = c -> {
            Pago pago = new Pago(c.getTotal());
            pago.registrarResultado(EstadoPago.RECHAZADO);
            return pago;
        };
        e.pagos(rechazado).pagar(compra, MetodoPago.TARJETA);
        verificar(compra.getEstado() == EstadoCompra.PENDIENTE, "Un rechazo no confirma la compra");
        verificar(e.consulta.consultarPorUsuario(e.usuario).isEmpty(), "Un rechazo no emite boletos");
        e.pagos(new PagoTarjeta()).pagar(compra, MetodoPago.TARJETA);
        verificar(compra.getPagos().size() == 2, "Se conservan los dos intentos de pago");
        verificar(e.consulta.consultarPorUsuario(e.usuario).size() == 1, "El reintento aprobado emite el boleto");
    }

    private static void reservaVencida() {
        Escenario e = new Escenario(1, 2);
        LocalDateTime ahora = LocalDateTime.now();
        Reserva reserva = new Reserva(100, e.usuario, e.presentacion, e.presentacion.getBoletos(),
                ahora.minusMinutes(20), ahora.minusMinutes(5));
        try (GestorReservas gestor = new GestorReservas(List.of(reserva))) {
            gestor.verificarVencimientos();
            verificar(reserva.getEstado() == EstadoReserva.VENCIDA, "Vencimiento de reserva");
            verificar(e.presentacion.consultarDisponibilidad() == 1, "Vencer devuelve los boletos al inventario");
            rechaza(IllegalStateException.class, reserva::confirmarPago);
            rechaza(IllegalStateException.class, () -> e.compras.crearCompra(reserva));
        }
    }

    private static void formasDePagoYNotificaciones() {
        for (ProcesadorPago procesador : List.of(new PagoTarjeta(), new PagoTransferencia(), new PagoMovil())) {
            Escenario e = new Escenario(1, 2);
            Compra compra = e.compras.crearCompra(e.reservas.reservar(e.usuario, e.presentacion, TipoBoleto.GENERAL, 1));
            verificar(e.pagos(procesador).pagar(compra, MetodoPago.TARJETA).getEstado() == EstadoPago.APROBADO,
                    "Procesador de pago simulado");
        }
        Escenario e = new Escenario(1, 2);
        AtomicInteger enviados = new AtomicInteger();
        Notificacion aviso = new Notificacion("Confirmacion", e.usuario, CanalNotificacion.CORREO,
                (mensaje, destinatario) -> {
                    verificar(mensaje.equals("Confirmacion") && destinatario.equals(e.usuario.getCorreo()), "Datos de notificacion");
                    enviados.incrementAndGet();
                });
        aviso.enviar();
        verificar(enviados.get() == 1, "Notificacion enviada mediante la interfaz");
        for (Notificador canal : List.of(new NotificadorCorreo(), new NotificadorSMS(), new NotificadorMensajeria())) {
            rechaza(IllegalArgumentException.class, () -> canal.enviar("", "destino"));
        }
    }

    private static void reembolsosEIncidentes() {
        Pago pago = new Pago(50);
        pago.registrarResultado();
        Reembolso reembolso = new Reembolso(pago, 30);
        reembolso.procesar();
        verificar(reembolso.getEstado() == EstadoReembolso.PROCESADO, "Reembolso simulado");
        rechaza(IllegalStateException.class, reembolso::procesar);
        rechaza(IllegalStateException.class, () -> new Reembolso(pago, 30).procesar());
        Incidente incidente = new Incidente(new Usuario(1, "Martin", "martin@example.com", "demo"), "No abre el boleto");
        incidente.escalar();
        verificar(incidente.getEstado() == EstadoIncidente.ESCALADO, "Incidente escalado");
        incidente.resolver();
        verificar(incidente.getEstado() == EstadoIncidente.RESUELTO, "Incidente resuelto");
    }

    private static void compradoresSimultaneos() throws InterruptedException {
        Escenario e = new Escenario(1, 2);
        CountDownLatch inicio = new CountDownLatch(1);
        AtomicInteger exitos = new AtomicInteger();
        AtomicInteger rechazos = new AtomicInteger();
        AtomicInteger errores = new AtomicInteger();
        List<Thread> hilos = new ArrayList<>();
        for (Usuario usuario : List.of(e.usuario, e.otro)) {
            Thread hilo = new Thread(() -> {
                try {
                    inicio.await();
                    e.reservas.reservar(usuario, e.presentacion, TipoBoleto.GENERAL, 1);
                    exitos.incrementAndGet();
                } catch (IllegalStateException ex) {
                    rechazos.incrementAndGet();
                } catch (Exception ex) {
                    errores.incrementAndGet();
                }
            });
            hilos.add(hilo);
            hilo.start();
        }
        inicio.countDown();
        for (Thread hilo : hilos) hilo.join();
        verificar(exitos.get() == 1 && rechazos.get() == 1 && errores.get() == 0,
                "Un solo comprador reserva el ultimo boleto");
    }

    private static void reservaCanceladaDuranteElPago() {
        Escenario e = new Escenario(1, 2);
        Reserva reserva = e.reservas.reservar(e.usuario, e.presentacion, TipoBoleto.GENERAL, 1);
        Compra compra = e.compras.crearCompra(reserva);
        ProcesadorPago procesador = c -> {
            reserva.cancelar();
            return new PagoTarjeta().procesar(c);
        };
        rechaza(IllegalStateException.class, () -> e.pagos(procesador).pagar(compra, MetodoPago.TARJETA));
        Pago pago = compra.getPagos().get(0);
        verificar(pago.getReembolsos().size() == 1, "El pago se devuelve si no se puede confirmar la reserva");
        verificar(pago.getReembolsos().get(0).getMonto() == compra.getTotal(), "Se devuelve el total");
        verificar(compra.getEstado() == EstadoCompra.CANCELADA, "La compra queda cancelada");
        verificar(e.consulta.consultarPorUsuario(e.usuario).isEmpty(), "No se emiten boletos al cancelar");
    }

    private static void falloDeNotificacion() {
        Escenario e = new Escenario(1, 2);
        Compra compra = e.compras.crearCompra(e.reservas.reservar(e.usuario, e.presentacion, TipoBoleto.GENERAL, 1));
        PagoService servicio = new PagoService(Map.of(MetodoPago.MOVIL, new PagoMovil()),
                (mensaje, destino) -> { throw new IllegalStateException("Canal caido"); });
        servicio.pagar(compra, MetodoPago.MOVIL);
        verificar(compra.getEstado() == EstadoCompra.CONFIRMADA, "Fallar al notificar no revierte el pago");
        rechaza(IllegalStateException.class, () -> servicio.pagar(compra, MetodoPago.MOVIL));
    }

    private static void reembolsosSimultaneos() throws InterruptedException {
        Pago pago = new Pago(50);
        pago.registrarResultado();
        CountDownLatch inicio = new CountDownLatch(1);
        AtomicInteger exitos = new AtomicInteger();
        AtomicInteger rechazos = new AtomicInteger();
        AtomicInteger errores = new AtomicInteger();
        List<Thread> hilos = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Thread hilo = new Thread(() -> {
                try {
                    inicio.await();
                    new Reembolso(pago, 40).procesar();
                    exitos.incrementAndGet();
                } catch (IllegalStateException ex) {
                    rechazos.incrementAndGet();
                } catch (Exception ex) {
                    errores.incrementAndGet();
                }
            });
            hilos.add(hilo);
            hilo.start();
        }
        inicio.countDown();
        for (Thread hilo : hilos) hilo.join();
        verificar(exitos.get() == 1 && rechazos.get() == 1 && errores.get() == 0,
                "Dos devoluciones no pueden superar juntas el monto pagado");
    }

    public static void main(String[] args) throws InterruptedException {
        buscarEventos();
        compraCompleta();
        limitesYCupos();
        cancelarReservaYCompra();
        pagoRechazadoYReintento();
        reservaVencida();
        formasDePagoYNotificaciones();
        reembolsosEIncidentes();
        compradoresSimultaneos();
        reservaCanceladaDuranteElPago();
        falloDeNotificacion();
        reembolsosSimultaneos();
        System.out.println("Pruebas correctas: " + verificaciones + " verificaciones");
    }
}
