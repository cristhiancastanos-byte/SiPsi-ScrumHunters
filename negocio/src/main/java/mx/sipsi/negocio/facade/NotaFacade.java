package mx.sipsi.negocio.facade;

import mx.sipsi.entity.NotaEntity;
import mx.sipsi.negocio.integration.INotaNegocioIntegration;
import mx.sipsi.negocio.integration.NotaNegocioIntegrationImpl;

public class NotaFacade {

    private final INotaNegocioIntegration notaNegocioIntegration;

    public NotaFacade() {
        this.notaNegocioIntegration = new NotaNegocioIntegrationImpl();
    }

    public void agregarNota(NotaEntity nota) throws Exception {
        if (nota == null) {
            throw new Exception("La nota no puede estar vacía.");
        }

        if (nota.getContenido() == null || nota.getContenido().trim().isEmpty()) {
            throw new Exception("La nota no puede estar vacía.");
        }

        if (nota.getPaciente() == null) {
            throw new Exception("No se encontró el paciente asociado a la nota.");
        }

        nota.setContenido(nota.getContenido().trim());

        if (nota.getTitulo() != null) {
            nota.setTitulo(nota.getTitulo().trim());
        }

        notaNegocioIntegration.agregarNota(nota);
    }
}