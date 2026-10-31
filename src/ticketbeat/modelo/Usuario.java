package ticketbeat.modelo;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private final int idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;
    private final List<Compra> compras = new ArrayList<>();

    public Usuario(int idUsuario, String nombre, String correo, String contrasena) {
        if (idUsuario <= 0) throw new IllegalArgumentException("ID de usuario invalido");
        this.idUsuario = idUsuario;
        actualizarDatos(nombre, correo);
        cambiarContrasena(contrasena);
    }

    public void actualizarDatos() { validarDatos(nombre, correo); }

    public void actualizarDatos(String nombre, String correo) {
        validarDatos(nombre, correo);
        this.nombre = nombre;
        this.correo = correo;
    }

    private void validarDatos(String nombre, String correo) {
        if (nombre == null || nombre.isBlank() || correo == null || !correo.contains("@")) {
            throw new IllegalArgumentException("Nombre o correo invalidos");
        }
    }

    public void cambiarContrasena() {
        throw new IllegalArgumentException("Use cambiarContrasena(String nuevaContrasena)");
    }

    public void cambiarContrasena(String nuevaContrasena) {
        if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
            throw new IllegalArgumentException("La contrasena no puede estar vacia");
        }
        contrasena = nuevaContrasena;
    }

    public void registrarCompra(Compra compra) {
        if (compra == null || compra.getUsuario() != this) {
            throw new IllegalArgumentException("La compra no pertenece a este usuario");
        }
        if (!compras.contains(compra)) compras.add(compra);
    }

    public List<Compra> getCompras() { return List.copyOf(compras); }
    public int getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
}
