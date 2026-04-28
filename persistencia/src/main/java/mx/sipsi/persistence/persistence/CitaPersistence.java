package mx.sipsi.persistence.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import mx.sipsi.entity.CitaEntity;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CitaPersistence {

    private EntityManagerFactory emf;

    public CitaPersistence() {
        this.emf = Persistence.createEntityManagerFactory("SipsiPU");
    }

    public void executeTransaction(CitaEntity cita) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cita);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al registrar la cita en la base de datos: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public CitaEntity executeFindEmpalme(Date fecha, Time horaInicio) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM CitaEntity c WHERE c.fecha = :fecha AND c.horaInicio = :horaInicio",
                            CitaEntity.class
                    )
                    .setParameter("fecha", fecha)
                    .setParameter("horaInicio", horaInicio)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<CitaEntity> executeSelectCitasPorMes(int mes, int anio) {
        EntityManager em = emf.createEntityManager();

        try {
            Calendar inicioMes = Calendar.getInstance();
            inicioMes.clear();
            inicioMes.set(Calendar.YEAR, anio);
            inicioMes.set(Calendar.MONTH, mes - 1);
            inicioMes.set(Calendar.DAY_OF_MONTH, 1);

            Calendar inicioMesSiguiente = Calendar.getInstance();
            inicioMesSiguiente.clear();
            inicioMesSiguiente.set(Calendar.YEAR, anio);
            inicioMesSiguiente.set(Calendar.MONTH, mes - 1);
            inicioMesSiguiente.set(Calendar.DAY_OF_MONTH, 1);
            inicioMesSiguiente.add(Calendar.MONTH, 1);

            TypedQuery<CitaEntity> query = em.createQuery(
                    "SELECT c FROM CitaEntity c " +
                            "WHERE c.fecha >= :inicioMes " +
                            "AND c.fecha < :inicioMesSiguiente " +
                            "ORDER BY c.fecha ASC, c.horaInicio ASC",
                    CitaEntity.class
            );

            query.setParameter("inicioMes", inicioMes.getTime());
            query.setParameter("inicioMesSiguiente", inicioMesSiguiente.getTime());

            return query.getResultList();

        } catch (Exception e) {
            throw new RuntimeException("Error al consultar las citas por mes: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public boolean executeTieneCitasPendientesPorPaciente(int idPaciente) {
        EntityManager em = emf.createEntityManager();

        try {
            Date fechaActual = new Date();

            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(c) FROM CitaEntity c " +
                            "WHERE c.idPaciente = :idPaciente " +
                            "AND c.fecha >= :fechaActual",
                    Long.class
            );

            query.setParameter("idPaciente", idPaciente);
            query.setParameter("fechaActual", fechaActual);

            return query.getSingleResult() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Error al validar citas pendientes del paciente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void executeDeleteCitasPendientesPorPaciente(int idPaciente) {
        EntityManager em = emf.createEntityManager();

        try {
            Date fechaActual = new Date();

            em.getTransaction().begin();

            em.createQuery(
                            "DELETE FROM CitaEntity c " +
                                    "WHERE c.idPaciente = :idPaciente " +
                                    "AND c.fecha >= :fechaActual"
                    )
                    .setParameter("idPaciente", idPaciente)
                    .setParameter("fechaActual", fechaActual)
                    .executeUpdate();

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar citas pendientes del paciente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}