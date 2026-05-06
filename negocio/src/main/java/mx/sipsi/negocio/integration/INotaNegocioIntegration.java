package mx.sipsi.negocio.integration;

import mx.sipsi.entity.NotaEntity;

public interface INotaNegocioIntegration {

    void agregarNota(NotaEntity nota) throws Exception;
}