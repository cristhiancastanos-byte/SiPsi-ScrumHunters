package mx.sipsi.negocio.integration;

import mx.sipsi.entity.ImagenReporteEntity;
import java.util.List;

public interface IImagenReporteNegocioIntegration {

    void guardarImagen(ImagenReporteEntity imagen) throws Exception;

    List<ImagenReporteEntity> listarImagenesPorReporte(Integer idReporte) throws Exception;

    ImagenReporteEntity consultarImagenPorId(Integer idImagen) throws Exception;

    void eliminarImagen(Integer idImagenReporte) throws Exception;
}