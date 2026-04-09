package mx.sipsi.persistence.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.NoResultException;
import mx.sipsi.entity.CitaEntity;
import java.sql.Time;
import java.util.Date;

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
            return em.createQuery("SELECT c FROM CitaEntity c WHERE c.fecha = :fecha AND c.horaInicio = :horaInicio", CitaEntity.class)
                    .setParameter("fecha", fecha)
                    .setParameter("horaInicio", horaInicio)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}