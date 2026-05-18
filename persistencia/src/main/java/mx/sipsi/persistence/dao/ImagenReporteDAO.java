package mx.sipsi.persistence.dao;

import mx.sipsi.entity.ImagenReporteEntity;
import mx.sipsi.persistence.integration.IImagenReportePersistenceIntegration;
import mx.sipsi.persistence.persistence.ImagenReportePersistence;

import java.util.List;

public class ImagenReporteDAO implements IImagenReportePersistenceIntegration {

    private ImagenReportePersistence imagenReportePersistence;

    public ImagenReporteDAO() {
        this.imagenReportePersistence = new ImagenReportePersistence();
    }

    @Override
    public void guardarImagen(ImagenReporteEntity imagen) throws Exception {
        imagenReportePersistence.executePersistImagen(imagen);
    }

    @Override
    public List<ImagenReporteEntity> listarImagenesPorReporte(Integer idReporte) throws Exception {
        return imagenReportePersistence.executeSelectImagenesPorReporte(idReporte);
    }

    @Override
    public ImagenReporteEntity consultarImagenPorId(Integer idImagen) throws Exception {
        return imagenReportePersistence.executeSelectImagenById(idImagen);
    }

    @Override
    public void eliminarImagen(Integer idImagenReporte) throws Exception {
        imagenReportePersistence.executeDeleteImagen(idImagenReporte);
    }
}