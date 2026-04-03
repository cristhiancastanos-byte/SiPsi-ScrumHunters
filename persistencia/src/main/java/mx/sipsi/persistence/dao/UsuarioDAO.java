package mx.sipsi.persistence.dao;

import mx.sipsi.entity.UsuarioEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class UsuarioDAO {

    private EntityManagerFactory emf;
    private EntityManager em;

    public UsuarioDAO() {
        this.emf = Persistence.createEntityManagerFactory("SiPsiPU");
        this.em = emf.createEntityManager();
    }

    public UsuarioEntity login(String correo, String pass) {
        try {

            String hql = "FROM UsuarioEntity u WHERE u.correo_electronico = :correo AND u.password = :pass";

            TypedQuery<UsuarioEntity> query = em.createQuery(hql, UsuarioEntity.class);
            query.setParameter("correo", correo);
            query.setParameter("pass", pass);

            List<UsuarioEntity> resultados = query.getResultList();

            return resultados.isEmpty() ? null : resultados.get(0);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void cerrar() {
        if (em != null) em.close();
        if (emf != null) emf.close();
    }
}