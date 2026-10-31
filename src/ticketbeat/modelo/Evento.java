package ticketbeat.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Evento {
    private final int idEvento;
    private final String nombre;
    private final String descripcion;
    private final String generoMusical;
    private final List<Presentacion> presentaciones = new ArrayList<>();
    private final List<Artista> artistas;
    private final PoliticaOrganizador politica;

    public Evento(int idEvento, String nombre, String descripcion, String generoMusical,
            List<Artista> artistas, PoliticaOrganizador politica) {
        if (idEvento <= 0 || nombre == null || nombre.isBlank() || descripcion == null
                || generoMusical == null || generoMusical.isBlank()) {
            throw new IllegalArgumentException("Datos del evento invalidos");
        }
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.generoMusical = generoMusical;
        this.artistas = List.copyOf(artistas);
        this.politica = Objects.requireNonNull(politica);
    }

    public void agregarPresentacion(Presentacion p) {
        Objects.requireNonNull(p);
        if (p.getEvento() != this || presentaciones.stream()
                .anyMatch(actual -> actual.getIdPresentacion() == p.getIdPresentacion())) {
            throw new IllegalArgumentException("Presentacion de otro evento o repetida");
        }
        presentaciones.add(p);
    }

    public List<Presentacion> obtenerPresentaciones() { return List.copyOf(presentaciones); }
    public List<Artista> getArtistas() { return artistas; }
    public PoliticaOrganizador getPolitica() { return politica; }
    public int getIdEvento() { return idEvento; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getGeneroMusical() { return generoMusical; }
}
