package mx.sipsi.persistence.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import mx.sipsi.entity.ArchivoEntity;

public class ArchivoPersistence {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("SipsiPU");

    public void guardarRutaArchivo(ArchivoEntity archivo) throws Exception {
        EntityManager entityManager = null;

        try {
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();

            entityManager.persist(archivo);

            entityManager.getTransaction().commit();

        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            throw new Exception("Error al guardar la ruta del archivo en la base de datos.", e);

        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}