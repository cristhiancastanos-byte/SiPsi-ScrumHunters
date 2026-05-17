package mx.sipsi.persistence.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import mx.sipsi.entity.ArchivoEntity;

import java.util.List;

public class ArchivoPersistence {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("SipsiPU");

    public ArchivoEntity guardarRutaArchivo(ArchivoEntity archivo) throws Exception {
        EntityManager entityManager = null;

        try {
            entityManager = emf.createEntityManager();
            entityManager.getTransaction().begin();

            entityManager.persist(archivo);

            entityManager.getTransaction().commit();

            return archivo;

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

    public ArchivoEntity buscarPorId(Long idArchivo) throws Exception {
        EntityManager entityManager = null;

        try {
            entityManager = emf.createEntityManager();
            return entityManager.find(ArchivoEntity.class, idArchivo);

        } catch (Exception e) {
            throw new Exception("Error al buscar el archivo por id.", e);

        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public List<ArchivoEntity> listarPorPaciente(int idPaciente) throws Exception {
        EntityManager entityManager = null;

        try {
            entityManager = emf.createEntityManager();

            String hql = "FROM ArchivoEntity a "
                    + "WHERE a.paciente.id = :idPaciente "
                    + "ORDER BY a.fechaSubida DESC";

            TypedQuery<ArchivoEntity> query = entityManager.createQuery(hql, ArchivoEntity.class);
            query.setParameter("idPaciente", idPaciente);

            return query.getResultList();

        } catch (Exception e) {
            throw new Exception("Error al listar archivos del paciente.", e);

        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

}