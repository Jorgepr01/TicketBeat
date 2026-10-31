package ticketbeat.modelo;

public class PoliticaOrganizador {
    private final String politicaDevolucion;
    private final String politicaCambios;
    private final int maxBoletosPorUsuario;
    private final boolean devolucionesPermitidas;
    private final boolean cambiosPermitidos;

    public PoliticaOrganizador(String politicaDevolucion, String politicaCambios,
            int maxBoletosPorUsuario, boolean devolucionesPermitidas, boolean cambiosPermitidos) {
        if (maxBoletosPorUsuario <= 0 || politicaDevolucion == null || politicaCambios == null) {
            throw new IllegalArgumentException("Politica invalida");
        }
        this.politicaDevolucion = politicaDevolucion;
        this.politicaCambios = politicaCambios;
        this.maxBoletosPorUsuario = maxBoletosPorUsuario;
        this.devolucionesPermitidas = devolucionesPermitidas;
        this.cambiosPermitidos = cambiosPermitidos;
    }

    public boolean permiteDevolucion() { return devolucionesPermitidas; }
    public boolean permiteCambio() { return cambiosPermitidos; }
    public boolean validarLimite(int cantidad) { return cantidad > 0 && cantidad <= maxBoletosPorUsuario; }
    public int getMaxBoletosPorUsuario() { return maxBoletosPorUsuario; }
    public String getPoliticaDevolucion() { return politicaDevolucion; }
    public String getPoliticaCambios() { return politicaCambios; }
}
