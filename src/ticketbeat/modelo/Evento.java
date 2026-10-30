package ticketbeat.modelo;

import java.util.List;

public class Evento {
    private int idEvento;
    private String nombre;
    private String descripcion;
    private String generoMusical;
    private List<Presentacion> presentaciones;
    private List<Artista> artistas;
    private PoliticaOrganizador politica;

    public void agregarPresentacion(Presentacion p) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public List<Presentacion> obtenerPresentaciones() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
