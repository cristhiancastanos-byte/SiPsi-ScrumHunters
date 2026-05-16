package mx.sipsi.negocio.integration;

import mx.sipsi.entity.NotaEntity;

public interface INotaNegocioIntegration {

    void agregarNota(NotaEntity nota) throws Exception;

    NotaEntity consultarNotaPorId(int idNota) throws Exception;

    void actualizarNota(NotaEntity nota) throws Exception;

    void eliminarNota(int idNota) throws Exception;
}