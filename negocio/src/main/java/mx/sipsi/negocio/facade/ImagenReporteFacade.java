package mx.sipsi.negocio.facade;

import mx.sipsi.entity.ImagenReporteEntity;
import mx.sipsi.negocio.integration.IImagenReporteNegocioIntegration;
import mx.sipsi.negocio.integration.ImagenReporteNegocioIntegrationImpl;

import java.util.List;

public class ImagenReporteFacade {

    private IImagenReporteNegocioIntegration imagenReporteNegocioIntegration;

    public ImagenReporteFacade() {
        this.imagenReporteNegocioIntegration = new ImagenReporteNegocioIntegrationImpl();
    }

    public void procesarGuardadoImagen(ImagenReporteEntity imagen) throws Exception {
        imagenReporteNegocioIntegration.guardarImagen(imagen);
    }

    public List<ImagenReporteEntity> consultarImagenesPorReporte(Integer idReporte) throws Exception {
        return imagenReporteNegocioIntegration.listarImagenesPorReporte(idReporte);
    }

    public ImagenReporteEntity procesarConsultaImagenPorId(Integer idImagen) throws Exception {
        return imagenReporteNegocioIntegration.consultarImagenPorId(idImagen);
    }

    public void procesarEliminacionImagen(Integer idImagenReporte) throws Exception {
        imagenReporteNegocioIntegration.eliminarImagen(idImagenReporte);
    }
}