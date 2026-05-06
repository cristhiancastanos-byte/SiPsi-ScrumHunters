package mx.sipsi.negocio.integration;

import mx.sipsi.entity.NotaEntity;
import mx.sipsi.negocio.integration.INotaNegocioIntegration;
import mx.sipsi.persistence.dao.NotaDAO;
import mx.sipsi.persistence.integration.INotaPersistenciaIntegration;

public class NotaNegocioIntegrationImpl implements INotaNegocioIntegration {

    private final INotaPersistenciaIntegration notaPersistenciaIntegration;

    public NotaNegocioIntegrationImpl() {
        this.notaPersistenciaIntegration = new NotaDAO();
    }

    @Override
    public void agregarNota(NotaEntity nota) throws Exception {
        notaPersistenciaIntegration.guardarNota(nota);
    }
}