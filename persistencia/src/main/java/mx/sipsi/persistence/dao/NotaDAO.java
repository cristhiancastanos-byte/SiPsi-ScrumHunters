package mx.sipsi.persistence.dao;

import mx.sipsi.entity.NotaEntity;
import mx.sipsi.persistence.integration.INotaPersistenciaIntegration;
import mx.sipsi.persistence.persistence.NotaPersistence;

public class NotaDAO implements INotaPersistenciaIntegration {

    private final NotaPersistence notaPersistence;

    public NotaDAO() {
        this.notaPersistence = new NotaPersistence();
    }

    @Override
    public void guardarNota(NotaEntity nota) throws Exception {
        notaPersistence.executePersistNota(nota);
    }
}