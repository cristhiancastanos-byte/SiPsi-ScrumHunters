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

    public NotaEntity executeSelectNotaById(int idNota) throws Exception {
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();

            NotaEntity nota = entityManager.find(NotaEntity.class, idNota);

            if (nota == null) {
                throw new Exception("No se encontró la nota clínica seleccionada.");
            }

            return nota;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error al consultar la nota clínica: " + e.getMessage(), e);

        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void executeUpdateNota(NotaEntity nota) throws Exception {
        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            NotaEntity notaManaged = entityManager.find(NotaEntity.class, nota.getId());

            if (notaManaged == null) {
                throw new Exception("No se encontró la nota clínica que se desea actualizar.");
            }

            notaManaged.setContenido(nota.getContenido());

            if (nota.getTitulo() != null) {
                notaManaged.setTitulo(nota.getTitulo());
            }

            entityManager.merge(notaManaged);
            entityManager.flush();

            entityManager.getTransaction().commit();

        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }

            e.printStackTrace();
            throw new Exception("Error al actualizar la nota clínica: " + e.getMessage(), e);

        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}