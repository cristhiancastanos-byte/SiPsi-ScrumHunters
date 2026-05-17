package mx.sipsi.persistence.integration;

import mx.sipsi.entity.ImagenReporteEntity;
import java.util.List;

public interface IImagenReportePersistenceIntegration {

    void guardarImagen(ImagenReporteEntity imagen) throws Exception;

    List<ImagenReporteEntity> listarImagenesPorReporte(Integer idReporte) throws Exception;

    ImagenReporteEntity consultarImagenPorId(Integer idImagen) throws Exception;
}