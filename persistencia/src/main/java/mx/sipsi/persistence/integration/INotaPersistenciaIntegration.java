package mx.sipsi.persistence.integration;

import mx.sipsi.entity.NotaEntity;

public interface INotaPersistenciaIntegration {

    void guardarNota(NotaEntity nota) throws Exception;
}