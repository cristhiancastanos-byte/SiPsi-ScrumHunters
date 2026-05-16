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
                            "SELECT c FROM CitaEntity c " +
                                    "WHERE c.fecha = :fecha " +
                                    "AND c.horaInicio = :horaInicio " +
                                    "AND (c.estado IS NULL OR c.estado <> 'Cancelada')",
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

    public boolean executeExisteTraslape(Date fecha, Time horaInicio, Time horaFin) {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(c) FROM CitaEntity c " +
                            "WHERE c.fecha = :fecha " +
                            "AND c.horaInicio < :horaFin " +
                            "AND c.horaFin > :horaInicio " +
                            "AND (c.estado IS NULL OR c.estado <> 'Cancelada')",
                    Long.class
            );

            query.setParameter("fecha", fecha);
            query.setParameter("horaInicio", horaInicio);
            query.setParameter("horaFin", horaFin);

            return query.getSingleResult() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Error al validar traslape de horario: " + e.getMessage(), e);

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
                            "AND c.fecha >= :fechaActual " +
                            "AND (c.estado IS NULL OR c.estado <> 'Cancelada')",
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
                                    "AND c.fecha >= :fechaActual " +
                                    "AND (c.estado IS NULL OR c.estado <> 'Cancelada')"
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

    public CitaEntity executeSelectCitaById(Integer idCita) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(CitaEntity.class, idCita);

        } catch (Exception e) {
            throw new RuntimeException("Error al consultar la cita seleccionada: " + e.getMessage(), e);

        } finally {
            em.close();
        }
    }

    public void executeCancelarCita(Integer idCita, String motivo) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            CitaEntity citaExistente = em.find(CitaEntity.class, idCita);

            if (citaExistente == null) {
                throw new RuntimeException("No se encontró la cita que se desea cancelar.");
            }

            citaExistente.setEstado("Cancelada");
            citaExistente.setMotivo(motivo);

            em.merge(citaExistente);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new RuntimeException("Error al cancelar la cita: " + e.getMessage(), e);

        } finally {
            em.close();
        }
    }

    public void executeUpdateCita(CitaEntity cita) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            CitaEntity citaExistente = em.find(CitaEntity.class, cita.getIdCita());

            if (citaExistente == null) {
                throw new RuntimeException("No se encontró la cita que se desea actualizar.");
            }

            citaExistente.setFecha(cita.getFecha());
            citaExistente.setHoraInicio(cita.getHoraInicio());
            citaExistente.setHoraFin(cita.getHoraFin());
            citaExistente.setMotivo(cita.getMotivo());
            citaExistente.setEstado(cita.getEstado());

            em.merge(citaExistente);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new RuntimeException("Error al actualizar la cita: " + e.getMessage(), e);

        } finally {
            em.close();
        }
    }

    public boolean executeExisteTraslapeParaEdicion(Date fecha, Time horaInicio, Time horaFin, Integer idCita) {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(c) FROM CitaEntity c " +
                            "WHERE c.fecha = :fecha " +
                            "AND c.idCita <> :idCita " +
                            "AND c.horaInicio < :horaFin " +
                            "AND c.horaFin > :horaInicio " +
                            "AND (c.estado IS NULL OR c.estado <> 'Cancelada')",
                    Long.class
            );

            query.setParameter("fecha", fecha);
            query.setParameter("idCita", idCita);
            query.setParameter("horaInicio", horaInicio);
            query.setParameter("horaFin", horaFin);

            return query.getSingleResult() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Error al validar traslape para edición de cita: " + e.getMessage(), e);

        } finally {
            em.close();
        }
    }
}