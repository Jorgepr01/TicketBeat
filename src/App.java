import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import ticketbeat.enums.MetodoPago;
import ticketbeat.enums.TipoBoleto;
import ticketbeat.modelo.Artista;
import ticketbeat.modelo.Boleto;
import ticketbeat.modelo.Compra;
import ticketbeat.modelo.Evento;
import ticketbeat.modelo.Incidente;
import ticketbeat.modelo.Lugar;
import ticketbeat.modelo.PoliticaOrganizador;
import ticketbeat.modelo.Presentacion;
import ticketbeat.modelo.Reserva;
import ticketbeat.modelo.Usuario;
import ticketbeat.notificaciones.NotificadorCorreo;
import ticketbeat.pagos.PagoMovil;
import ticketbeat.pagos.PagoTarjeta;
import ticketbeat.pagos.PagoTransferencia;
import ticketbeat.servicios.BuscadorEventos;
import ticketbeat.servicios.CompraService;
import ticketbeat.servicios.ConsultaBoletosService;
import ticketbeat.servicios.GestorReservas;
import ticketbeat.servicios.PagoService;
import ticketbeat.servicios.ReservaService;

public class App {
    public static void main(String[] args) {
        Artista artista = new Artista(1, "Banda del Puerto", "Rock");
        PoliticaOrganizador politica = new PoliticaOrganizador(
                "Devoluciones sujetas a revision", "Cambios sujetos a disponibilidad", 4, true, true);
        Evento evento = new Evento(1, "Festival del Puerto", "Concierto de bandas locales", "Rock",
                List.of(artista), politica);
        Lugar lugar = new Lugar(1, "Teatro del Puerto", "Av. Principal", "Guayaquil", 100);
        Presentacion presentacion = new Presentacion(1, LocalDateTime.now().plusDays(30), lugar, evento,
                List.of(new Boleto(1, TipoBoleto.GENERAL, 25), new Boleto(2, TipoBoleto.GENERAL, 25),
                        new Boleto(3, TipoBoleto.GENERAL, 25), new Boleto(4, TipoBoleto.VIP, 60)));
        evento.agregarPresentacion(presentacion);
        Usuario usuario = new Usuario(1, "Martin", "martin@example.com", "clave-demo");
        BuscadorEventos buscador = new BuscadorEventos(List.of(evento));
        ReservaService reservas = new ReservaService();
        CompraService compras = new CompraService();
        ConsultaBoletosService consulta = new ConsultaBoletosService();
        PagoService pagos = new PagoService(Map.of(MetodoPago.TARJETA, new PagoTarjeta(),
                MetodoPago.TRANSFERENCIA, new PagoTransferencia(), MetodoPago.MOVIL, new PagoMovil()),
                new NotificadorCorreo());

        try (GestorReservas gestor = new GestorReservas(reservas.getReservas())) {
            System.out.println("TicketBeat - Grupo 6");
            System.out.println("Demostracion por consola. Pagos y notificaciones simulados.\n");

            System.out.println("1. Buscar evento");
            for (Evento resultado : buscador.buscarPorNombre("festival")) {
                System.out.println(resultado.getNombre() + " - " + resultado.getGeneroMusical());
            }
            System.out.println("Lugar: " + lugar.getNombre() + " - " + lugar.getLocalidad());
            System.out.println("Fecha: " + presentacion.getFechaHora());
            System.out.println("Sin coincidencias: " + buscador.buscarPorNombre("inexistente").size());

            System.out.println("\n2. Reservar entradas");
            Reserva reserva = reservas.reservar(usuario, presentacion, TipoBoleto.GENERAL, 2);
            System.out.println("Reserva " + reserva.getIdReserva() + ": " + reserva.getEstado());
            System.out.println("Vence: " + reserva.getFechaExpiracion());
            System.out.println("Disponibles: " + presentacion.consultarDisponibilidad());
            try {
                reservas.reservar(usuario, presentacion, TipoBoleto.GENERAL, 3);
            } catch (IllegalArgumentException ex) {
                System.out.println("Solicitud rechazada: " + ex.getMessage());
            }

            System.out.println("\n3. Pagar entradas");
            Compra compra = compras.crearCompra(reserva);
            pagos.pagar(compra, MetodoPago.TARJETA);
            System.out.println("Compra " + compra.getIdCompra() + ": " + compra.getEstado());

            System.out.println("\n4. Consultar boletos comprados");
            for (Boleto boleto : consulta.consultarPorUsuario(usuario)) {
                System.out.println(boleto.getCodigo() + " - " + boleto.getTipo()
                        + " - $" + boleto.getPrecio() + " - " + boleto.getEstado());
                System.out.println("Fecha: " + presentacion.getFechaHora() + ". Lugar: " + lugar.getNombre());
            }

            Incidente incidente = new Incidente(usuario, "No puedo abrir mi boleto digital");
            incidente.escalar();
            System.out.println("\nIncidente de ejemplo: " + incidente.getIdIncidente() + " - " + incidente.getEstado());
            gestor.verificarVencimientos();
        }
    }
}
