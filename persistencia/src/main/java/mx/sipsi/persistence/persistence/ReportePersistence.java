package mx.sipsi.persistence.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import mx.sipsi.entity.ReporteEntity;

public class ReportePersistence {

    private EntityManagerFactory emf;

    public ReportePersistence() {
        this.emf = Persistence.createEntityManagerFactory("SipsiPU");
    }

    public void executePersistReporte(ReporteEntity reporte) throws Exception {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            em.persist(reporte);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new Exception("Error al guardar el reporte clínico.", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean executeExisteReportePorCita(Integer idCita) throws Exception {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(r) FROM ReporteEntity r WHERE r.idCita = :idCita",
                    Long.class
            );

            query.setParameter("idCita", idCita);

            Long total = query.getSingleResult();

            return total != null && total > 0;
        } catch (Exception e) {
            throw new Exception("Error al validar si la cita ya tiene reporte.", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public ReporteEntity executeSelectReportePorCita(Integer idCita) throws Exception {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            TypedQuery<ReporteEntity> query = em.createQuery(
                    "SELECT r FROM ReporteEntity r WHERE r.idCita = :idCita",
                    ReporteEntity.class
            );

            query.setParameter("idCita", idCita);
            query.setMaxResults(1);

            return query.getResultStream().findFirst().orElse(null);
        } catch (Exception e) {
            throw new Exception("Error al consultar el reporte clínico de la cita.", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public ReporteEntity executeSelectReporteById(Integer idReporte) throws Exception {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            TypedQuery<ReporteEntity> query = em.createQuery(
                    "SELECT r FROM ReporteEntity r WHERE r.idReporte = :idReporte",
                    ReporteEntity.class
            );

            query.setParameter("idReporte", idReporte);
            query.setMaxResults(1);

            return query.getResultStream().findFirst().orElse(null);
        } catch (Exception e) {
            throw new Exception("Error al consultar el reporte clínico seleccionado.", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}