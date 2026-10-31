package ticketbeat.modelo;

public class Artista {
    private final int idArtista;
    private String nombre;
    private String generoMusical;

    public Artista(int idArtista, String nombre, String generoMusical) {
        if (idArtista <= 0) throw new IllegalArgumentException("ID de artista invalido");
        this.idArtista = idArtista;
        actualizarInformacion(nombre, generoMusical);
    }

    public void actualizarInformacion() {
        if (nombre == null || nombre.isBlank() || generoMusical == null || generoMusical.isBlank()) {
            throw new IllegalArgumentException("El artista necesita nombre y genero musical");
        }
    }

    public void actualizarInformacion(String nombre, String generoMusical) {
        if (nombre == null || nombre.isBlank() || generoMusical == null || generoMusical.isBlank()) {
            throw new IllegalArgumentException("El artista necesita nombre y genero musical");
        }
        this.nombre = nombre;
        this.generoMusical = generoMusical;
    }

    public int getIdArtista() { return idArtista; }
    public String getNombre() { return nombre; }
    public String getGeneroMusical() { return generoMusical; }
}
