package mx.sipsi.persistence.integration;


import mx.sipsi.entity.ArchivoEntity;

public interface IArchivoPersistenceIntegration {

    void guardarRutaArchivo(ArchivoEntity archivo) throws Exception;
}