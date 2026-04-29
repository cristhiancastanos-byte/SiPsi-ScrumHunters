package mx.sipsi.persistence.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import mx.sipsi.entity.NotaEntity;
import mx.sipsi.entity.PacienteEntity;

public class NotaPersistence {

    private final EntityManagerFactory entityManagerFactory;

    public NotaPersistence() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("SipsiPU");
    }

    public void executePersistNota(NotaEntity nota) throws Exception {
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            PacienteEntity pacienteManaged = entityManager.find(PacienteEntity.class, nota.getPaciente().getId());

            if (pacienteManaged == null) {
                throw new Exception("No se encontró el paciente en la base de datos.");
            }

            nota.setPaciente(pacienteManaged);

            entityManager.persist(nota);
            entityManager.flush();

            entityManager.getTransaction().commit();

        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            e.printStackTrace();
            throw new Exception("Error al guardar la nota clínica: " + e.getMessage(), e);

        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}