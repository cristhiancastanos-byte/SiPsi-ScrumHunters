package mx.sipsi.persistence.integration;

import mx.sipsi.entity.NotaEntity;

public interface INotaPersistenciaIntegration {

    void guardarNota(NotaEntity nota) throws Exception;

    NotaEntity consultarNotaPorId(int idNota) throws Exception;

    void actualizarNota(NotaEntity nota) throws Exception;
}