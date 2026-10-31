package ticketbeat.servicios;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import ticketbeat.modelo.Evento;

public class BuscadorEventos {
    private final List<Evento> eventos;

    public BuscadorEventos(List<Evento> eventos) { this.eventos = List.copyOf(eventos); }

    public List<Evento> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Ingrese un nombre");
        String criterio = nombre.trim().toLowerCase(Locale.ROOT);
        return eventos.stream().filter(e -> e.getNombre().toLowerCase(Locale.ROOT).contains(criterio)).toList();
    }

    public List<Evento> buscarPorArtista(int idArtista) {
        return eventos.stream().filter(e -> e.getArtistas().stream()
                .anyMatch(a -> a.getIdArtista() == idArtista)).toList();
    }

    public List<Evento> buscarPorFecha(LocalDate fecha) {
        Objects.requireNonNull(fecha);
        return eventos.stream().filter(e -> e.obtenerPresentaciones().stream()
                .anyMatch(p -> p.getFechaHora().toLocalDate().equals(fecha))).toList();
    }

    public List<Evento> buscarPorLugar(int idLugar) {
        return eventos.stream().filter(e -> e.obtenerPresentaciones().stream()
                .anyMatch(p -> p.getLugar().getIdLugar() == idLugar)).toList();
    }

    public List<Evento> buscarPorGenero(String genero) {
        if (genero == null || genero.isBlank()) throw new IllegalArgumentException("Ingrese un genero musical");
        return eventos.stream().filter(e -> e.getGeneroMusical().equalsIgnoreCase(genero.trim())).toList();
    }
}
