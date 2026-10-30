package ticketbeat.modelo;

public class PoliticaOrganizador {
    private String politicaDevolucion;
    private String politicaCambios;
    private int maxBoletosPorUsuario;

    public boolean permiteDevolucion() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public boolean permiteCambio() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public boolean validarLimite(int cantidad) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}
