package mx.sipsi.persistence.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import mx.sipsi.entity.PacienteEntity;
import org.hibernate.Session;

public class PacientePersistence {

    private final EntityManagerFactory emf;

    public PacientePersistence() {
        try {
            this.emf = Persistence.createEntityManagerFactory("SipsiPU");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo crear EntityManagerFactory para SipsiPU: " + e.getMessage(), e);
        }
    }

    public void executeTransaction(PacienteEntity paciente) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(paciente);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean checkExists(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }

        EntityManager em = emf.createEntityManager();
        try {
            String hql = "SELECT COUNT(p) FROM PacienteEntity p WHERE LOWER(TRIM(p.correo)) = LOWER(TRIM(:correoParam))";
            TypedQuery<Long> query = em.createQuery(hql, Long.class);
            query.setParameter("correoParam", correo);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    public List<PacienteEntity> executeFindAllActivos() throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            String hql = "FROM PacienteEntity p WHERE p.activo = true ORDER BY p.nombre ASC";
            TypedQuery<PacienteEntity> query = em.createQuery(hql, PacienteEntity.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<PacienteEntity> executeFindByNombreActivos(String nombre) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            String hql = "FROM PacienteEntity p "
                    + "WHERE p.activo = true "
                    + "AND LOWER(TRIM(p.nombre)) LIKE LOWER(:nombreParam) "
                    + "ORDER BY p.nombre ASC";

            TypedQuery<PacienteEntity> query = em.createQuery(hql, PacienteEntity.class);
            query.setParameter("nombreParam", "%" + nombre.trim() + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public PacienteEntity executeSelectById(int id) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(PacienteEntity.class, id);
        } finally {
            em.close();
        }
    }

    public PacienteEntity executeFindDuplicado(String nombre, Date fechaNac, int idActual) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            String hql = "FROM PacienteEntity p "
                    + "WHERE LOWER(TRIM(p.nombre)) = LOWER(TRIM(:nombreParam)) "
                    + "AND p.fechaNac = :fechaNacParam "
                    + "AND p.id <> :idActualParam";

            TypedQuery<PacienteEntity> query = em.createQuery(hql, PacienteEntity.class);
            query.setParameter("nombreParam", nombre);
            query.setParameter("fechaNacParam", fechaNac);
            query.setParameter("idActualParam", idActual);
            query.setMaxResults(1);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void executeUpdate(PacienteEntity paciente) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Session session = em.unwrap(Session.class);
            session.merge(paciente);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    public boolean executeBajaLogica(int idPaciente) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            PacienteEntity paciente = em.find(PacienteEntity.class, idPaciente);

            if (paciente == null) {
                return false;
            }

            paciente.setActivo(false);
            em.merge(paciente);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void executeArchivarPaciente(int idPaciente) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            PacienteEntity paciente = em.find(PacienteEntity.class, idPaciente);

            if (paciente == null) {
                throw new RuntimeException("No se encontró el paciente");
            }

            paciente.setActivo(false);
            em.merge(paciente);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void executeRecuperarPaciente(int idPaciente) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            PacienteEntity paciente = em.find(PacienteEntity.class, idPaciente);

            if (paciente == null) {
                throw new RuntimeException("No se encontró el paciente");
            }

            paciente.setActivo(true);
            em.merge(paciente);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<PacienteEntity> executeFindAllArchivados() throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            String hql = "FROM PacienteEntity p WHERE p.activo = false ORDER BY p.nombre ASC";
            TypedQuery<PacienteEntity> query = em.createQuery(hql, PacienteEntity.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void executeDeletePaciente(int idPaciente) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            PacienteEntity paciente = em.find(PacienteEntity.class, idPaciente);

            if (paciente == null) {
                throw new RuntimeException("No se encontró el paciente");
            }

            em.remove(paciente);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}