package mx.sipsi.negocio.integration;

import mx.sipsi.entity.ImagenReporteEntity;
import mx.sipsi.persistence.dao.ImagenReporteDAO;

import java.util.List;

public class ImagenReporteNegocioIntegrationImpl implements IImagenReporteNegocioIntegration {

    private ImagenReporteDAO imagenReporteDAO;

    public ImagenReporteNegocioIntegrationImpl() {
        this.imagenReporteDAO = new ImagenReporteDAO();
    }

    @Override
    public void guardarImagen(ImagenReporteEntity imagen) throws Exception {
        validarImagen(imagen);

        imagenReporteDAO.guardarImagen(imagen);
    }

    @Override
    public List<ImagenReporteEntity> listarImagenesPorReporte(Integer idReporte) throws Exception {
        if (idReporte == null || idReporte <= 0) {
            throw new Exception("No se encontró el reporte clínico para consultar sus imágenes.");
        }

        return imagenReporteDAO.listarImagenesPorReporte(idReporte);
    }

    private void validarImagen(ImagenReporteEntity imagen) throws Exception {
        if (imagen == null) {
            throw new Exception("No se encontró la imagen que se desea guardar.");
        }

        if (imagen.getReporte() == null || imagen.getReporte().getIdReporte() == null) {
            throw new Exception("La imagen debe estar asociada a un reporte clínico.");
        }

        if (imagen.getNombreOriginal() == null || imagen.getNombreOriginal().trim().isEmpty()) {
            throw new Exception("El nombre original de la imagen es obligatorio.");
        }

        if (imagen.getRutaServidor() == null || imagen.getRutaServidor().trim().isEmpty()) {
            throw new Exception("La ruta de almacenamiento de la imagen es obligatoria.");
        }

        validarFormatoPermitido(imagen.getNombreOriginal());
    }

    private void validarFormatoPermitido(String nombreOriginal) throws Exception {
        String nombreMinusculas = nombreOriginal.toLowerCase();

        if (!(nombreMinusculas.endsWith(".jpg")
                || nombreMinusculas.endsWith(".jpeg")
                || nombreMinusculas.endsWith(".png"))) {
            throw new Exception("Formato no permitido. Solo se permiten imágenes JPG, JPEG o PNG.");
        }
    }

    @Override
    public ImagenReporteEntity consultarImagenPorId(Integer idImagen) throws Exception {

        if (idImagen == null) {
            throw new Exception("No se seleccionó una imagen válida.");
        }

        ImagenReporteEntity imagen = imagenReporteDAO.consultarImagenPorId(idImagen);

        if (imagen == null) {
            throw new Exception("No se encontró la imagen seleccionada.");
        }

        return imagen;
    }
}