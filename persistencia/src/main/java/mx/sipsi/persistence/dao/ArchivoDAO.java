package mx.sipsi.persistence.dao;

import mx.sipsi.entity.ArchivoEntity;
import mx.sipsi.persistence.integration.IArchivoPersistenceIntegration;
import mx.sipsi.persistence.persistence.ArchivoPersistence;

public class ArchivoDAO implements IArchivoPersistenceIntegration {

    private ArchivoPersistence archivoPersistence;

    public ArchivoDAO() {
        this.archivoPersistence = new ArchivoPersistence();
    }

    @Override
    public void guardarRutaArchivo(ArchivoEntity archivo) throws Exception {
        archivoPersistence.guardarRutaArchivo(archivo);
    }
}