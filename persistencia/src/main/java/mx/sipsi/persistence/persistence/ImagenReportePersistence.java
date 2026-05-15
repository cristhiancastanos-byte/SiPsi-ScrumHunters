package mx.sipsi.persistence.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import mx.sipsi.entity.ImagenReporteEntity;

import java.util.List;

public class ImagenReportePersistence {

    private EntityManagerFactory emf;

    public ImagenReportePersistence() {
        this.emf = Persistence.createEntityManagerFactory("SipsiPU");
    }

    public void executePersistImagen(ImagenReporteEntity imagen) throws Exception {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            em.persist(imagen);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Error al guardar la imagen del reporte clínico.", e);

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ImagenReporteEntity> executeSelectImagenesPorReporte(Integer idReporte) throws Exception {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            return em.createQuery(
                            "SELECT i FROM ImagenReporteEntity i WHERE i.reporte.idReporte = :idReporte ORDER BY i.fechaSubida DESC",
                            ImagenReporteEntity.class
                    )
                    .setParameter("idReporte", idReporte)
                    .getResultList();

        } catch (Exception e) {
            throw new Exception("Error al consultar las imágenes del reporte clínico.", e);

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}