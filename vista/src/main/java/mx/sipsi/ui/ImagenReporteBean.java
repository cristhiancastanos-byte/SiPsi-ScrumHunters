package mx.sipsi.ui;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.sipsi.entity.ImagenReporteEntity;
import mx.sipsi.entity.ReporteEntity;
import mx.sipsi.negocio.delegate.ImagenReporteDelegate;
import org.primefaces.event.FileUploadEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Named("imagenReporteBean")
@ViewScoped
public class ImagenReporteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final long TAMANO_MAXIMO = 5242880;

    private static final String RUTA_BASE_IMAGENES_REPORTE =
            System.getProperty("user.home") + File.separator + "sipsi_imagenes_reportes";

    private ReporteEntity reporteActual;
    private ImagenReporteDelegate imagenReporteDelegate;
    private List<ImagenReportePendiente> imagenesPendientes;

    public ImagenReporteBean() {
        this.imagenReporteDelegate = new ImagenReporteDelegate();
        this.imagenesPendientes = new ArrayList<>();
    }

    public void prepararCargaImagen(ReporteEntity reporte) {
        this.reporteActual = reporte;
        this.imagenesPendientes = new ArrayList<>();
    }

    public void handleImagenUpload(FileUploadEvent event) {
        try {
            if (event == null || event.getFile() == null) {
                agregarError("Error", "No se recibió ninguna imagen.");
                return;
            }

            if (event.getFile().getSize() > TAMANO_MAXIMO) {
                agregarError("Archivo demasiado pesado", "El archivo no debe superar 5MB.");
                return;
            }

            String nombreOriginal = event.getFile().getFileName();

            if (nombreOriginal == null || nombreOriginal.trim().isEmpty()) {
                agregarError("Error", "El nombre de la imagen no es válido.");
                return;
            }

            String nombreLower = nombreOriginal.toLowerCase();

            if (!(nombreLower.endsWith(".jpg") || nombreLower.endsWith(".jpeg") || nombreLower.endsWith(".png"))) {
                agregarError("Formato no válido", "Solo se permiten imágenes JPG, JPEG o PNG.");
                return;
            }

            ImagenReportePendiente imagenPendiente = new ImagenReportePendiente();
            imagenPendiente.setNombreOriginal(nombreOriginal);
            imagenPendiente.setTipoContenido(event.getFile().getContentType());
            imagenPendiente.setTamano(event.getFile().getSize());

            try (InputStream inputStream = event.getFile().getInputStream()) {
                imagenPendiente.setDatos(inputStream.readAllBytes());
            }

            imagenesPendientes.add(0, imagenPendiente);

            agregarInfo("Imagen agregada", "La imagen se agregó correctamente al reporte.");

        } catch (Exception e) {
            e.printStackTrace();
            agregarError("Error", "Ocurrió un problema al cargar la imagen.");
        }
    }

    public void guardarImagenesPendientes(ReporteEntity reporte) throws Exception {
        if (imagenesPendientes == null || imagenesPendientes.isEmpty()) {
            return;
        }

        if (reporte == null || reporte.getIdReporte() == null) {
            throw new Exception("No se encontró el reporte clínico para guardar la imagen.");
        }

        File carpetaDestino = new File(RUTA_BASE_IMAGENES_REPORTE);

        if (!carpetaDestino.exists()) {
            boolean carpetaCreada = carpetaDestino.mkdirs();

            if (!carpetaCreada) {
                throw new Exception("No se pudo crear la carpeta para guardar imágenes del reporte.");
            }
        }

        for (ImagenReportePendiente imagenPendiente : imagenesPendientes) {
            String nombreLimpio = limpiarNombreArchivo(imagenPendiente.getNombreOriginal());
            String nombreUnico = "Reporte_" + reporte.getIdReporte() + "_" + UUID.randomUUID() + "_" + nombreLimpio;

            File archivoFisico = new File(carpetaDestino, nombreUnico);

            try (FileOutputStream fileOutputStream = new FileOutputStream(archivoFisico)) {
                fileOutputStream.write(imagenPendiente.getDatos());
            }

            ImagenReporteEntity imagen = new ImagenReporteEntity();
            imagen.setNombreOriginal(imagenPendiente.getNombreOriginal());
            imagen.setRutaServidor(archivoFisico.getAbsolutePath());
            imagen.setFechaSubida(new Date());
            imagen.setReporte(reporte);

            imagenReporteDelegate.guardarImagen(imagen);
        }

        limpiarImagenesPendientes();
    }

    public void limpiarImagenesPendientes() {
        this.imagenesPendientes = new ArrayList<>();
        this.reporteActual = null;
    }

    private String limpiarNombreArchivo(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "imagen";
        }

        String limpio = Normalizer.normalize(texto, Normalizer.Form.NFD);
        limpio = limpio.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        limpio = limpio.replaceAll("[^a-zA-Z0-9.]", "_");

        return limpio;
    }

    private void agregarInfo(String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, resumen, detalle));
    }

    private void agregarError(String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, resumen, detalle));
    }

    public ReporteEntity getReporteActual() {
        return reporteActual;
    }

    public void setReporteActual(ReporteEntity reporteActual) {
        this.reporteActual = reporteActual;
    }

    public List<ImagenReportePendiente> getImagenesPendientes() {
        return imagenesPendientes;
    }

    public void setImagenesPendientes(List<ImagenReportePendiente> imagenesPendientes) {
        this.imagenesPendientes = imagenesPendientes;
    }

    public static class ImagenReportePendiente implements Serializable {

        private static final long serialVersionUID = 1L;

        private String nombreOriginal;
        private String tipoContenido;
        private long tamano;
        private byte[] datos;

        public String getVistaPreviaBase64() {
            if (datos == null || datos.length == 0) {
                return "";
            }

            String tipo = tipoContenido;

            if (tipo == null || tipo.trim().isEmpty()) {
                tipo = "image/png";
            }

            return "data:" + tipo + ";base64," + Base64.getEncoder().encodeToString(datos);
        }

        public String getTamanoTexto() {
            double mb = tamano / 1024.0 / 1024.0;
            return String.format("%.2f MB", mb);
        }

        public String getNombreOriginal() {
            return nombreOriginal;
        }

        public void setNombreOriginal(String nombreOriginal) {
            this.nombreOriginal = nombreOriginal;
        }

        public String getTipoContenido() {
            return tipoContenido;
        }

        public void setTipoContenido(String tipoContenido) {
            this.tipoContenido = tipoContenido;
        }

        public long getTamano() {
            return tamano;
        }

        public void setTamano(long tamano) {
            this.tamano = tamano;
        }

        public byte[] getDatos() {
            return datos;
        }

        public void setDatos(byte[] datos) {
            this.datos = datos;
        }
    }
}
