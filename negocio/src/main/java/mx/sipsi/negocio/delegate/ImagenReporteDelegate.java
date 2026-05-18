package mx.sipsi.negocio.delegate;

import mx.sipsi.entity.ImagenReporteEntity;
import mx.sipsi.negocio.facade.ImagenReporteFacade;

import java.util.List;

public class ImagenReporteDelegate {

    private ImagenReporteFacade imagenReporteFacade;

    public ImagenReporteDelegate() {
        this.imagenReporteFacade = new ImagenReporteFacade();
    }

    public void guardarImagen(ImagenReporteEntity imagen) throws Exception {
        imagenReporteFacade.procesarGuardadoImagen(imagen);
    }

    public List<ImagenReporteEntity> listarImagenesPorReporte(Integer idReporte) throws Exception {
        return imagenReporteFacade.consultarImagenesPorReporte(idReporte);
    }

    public ImagenReporteEntity consultarImagenPorId(Integer idImagen) throws Exception {
        return imagenReporteFacade.procesarConsultaImagenPorId(idImagen);
    }

    public void eliminarImagen(Integer idImagenReporte) throws Exception {
        imagenReporteFacade.procesarEliminacionImagen(idImagenReporte);
    }
}