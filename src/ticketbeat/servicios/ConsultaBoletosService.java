package ticketbeat.servicios;

import java.util.List;
import java.util.Objects;
import ticketbeat.modelo.Usuario;
import ticketbeat.modelo.Boleto;
import ticketbeat.enums.EstadoBoleto;
import ticketbeat.enums.EstadoCompra;

public class ConsultaBoletosService {
    public List<Boleto> consultarPorUsuario(Usuario usuario) {
        Objects.requireNonNull(usuario);
        return usuario.getCompras().stream().filter(c -> c.getEstado() == EstadoCompra.CONFIRMADA)
                .flatMap(c -> c.getBoletos().stream()).filter(b -> b.getEstado() == EstadoBoleto.VENDIDO)
                .distinct().toList();
    }
}
