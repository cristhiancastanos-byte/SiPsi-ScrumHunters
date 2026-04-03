package mx.sipsi.persistence.persistence;

import mx.sipsi.entity.PacienteEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class PacientePersistence {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SipsiPU");
    private EntityManager em = emf.createEntityManager();

    public void executeTransaction(PacienteEntity paciente) throws Exception {
        try {
            em.getTransaction().begin();
            em.persist(paciente);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    public boolean checkExists(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        String hql = "SELECT COUNT(p) FROM PacienteEntity p WHERE p.correo = :correoParam";
        TypedQuery<Long> query = em.createQuery(hql, Long.class);
        query.setParameter("correoParam", correo);
        return query.getSingleResult() > 0;
    }

    public boolean checkDuplicate(String nombre, java.util.Date fecha) {
        String hql = "SELECT COUNT(p) FROM PacienteEntity p WHERE p.nombre = :nombre AND p.fechaNac = :fecha";
        TypedQuery<Long> query = em.createQuery(hql, Long.class);
        query.setParameter("nombre", nombre);
        query.setParameter("fecha", fecha);
        return query.getSingleResult() > 0;
    }
}