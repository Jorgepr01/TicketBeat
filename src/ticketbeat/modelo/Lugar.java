package ticketbeat.modelo;

public class Lugar {
    private final int idLugar;
    private final String nombre;
    private final String direccion;
    private final String localidad;
    private final int capacidad;

    public Lugar(int idLugar, String nombre, String direccion, String localidad, int capacidad) {
        if (idLugar <= 0 || capacidad <= 0) throw new IllegalArgumentException("ID o capacidad invalidos");
        if (nombre == null || nombre.isBlank() || direccion == null || localidad == null) {
            throw new IllegalArgumentException("Faltan datos del lugar");
        }
        this.idLugar = idLugar;
        this.nombre = nombre;
        this.direccion = direccion;
        this.localidad = localidad;
        this.capacidad = capacidad;
    }

    public int consultarCapacidad() { return capacidad; }
    public int getIdLugar() { return idLugar; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getLocalidad() { return localidad; }
}
