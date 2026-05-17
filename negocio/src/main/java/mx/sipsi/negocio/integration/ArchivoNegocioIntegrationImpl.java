package mx.sipsi.negocio.integration;

import mx.sipsi.entity.ArchivoEntity;
import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.persistence.dao.ArchivoDAO;
import mx.sipsi.persistence.integration.IArchivoPersistenceIntegration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ArchivoNegocioIntegrationImpl implements IArchivoNegocioIntegration {

    private static final String RUTA_BASE_ARCHIVOS =
            System.getProperty("user.home") + File.separator + "sipsi_archivos";

    private IArchivoPersistenceIntegration archivoPersistenciaIntegration;

    public ArchivoNegocioIntegrationImpl() {
        this.archivoPersistenciaIntegration = new ArchivoDAO();
    }

    @Override
    public ArchivoEntity subirArchivo(InputStream inputStream, String nombreOriginal, PacienteEntity paciente) throws IOException {

        if (inputStream == null) {
            throw new IOException("No se recibió ningún archivo.");
        }

        if (nombreOriginal == null || nombreOriginal.trim().isEmpty()) {
            throw new IOException("El nombre del archivo no es válido.");
        }

        if (paciente == null || paciente.getId() <= 0) {
            throw new IOException("No se encontró el paciente asociado al archivo.");
        }

        String nombreLower = nombreOriginal.toLowerCase();

        if (!(nombreLower.endsWith(".jpg") || nombreLower.endsWith(".jpeg") || nombreLower.endsWith(".png"))) {
            throw new IOException("Formato no válido. Solo se permiten imágenes JPG o PNG.");
        }

        File carpetaDestino = new File(RUTA_BASE_ARCHIVOS);

        if (!carpetaDestino.exists()) {
            boolean carpetaCreada = carpetaDestino.mkdirs();

            if (!carpetaCreada) {
                throw new IOException("No se pudo crear la carpeta para guardar archivos.");
            }
        }

        String nombrePaciente = limpiarNombreArchivo(paciente.getNombre());
        String nombreUnico = nombrePaciente + "_" + UUID.randomUUID().toString() + "_" + nombreOriginal;
        File archivoFisico = new File(carpetaDestino, nombreUnico);

        try (FileOutputStream fileOutputStream = new FileOutputStream(archivoFisico)) {

            byte[] buffer = new byte[1024];
            int bytesLeidos;

            while ((bytesLeidos = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesLeidos);
            }

        } catch (IOException e) {
            throw new IOException("Error al guardar físicamente el archivo en el servidor.", e);
        }

        ArchivoEntity archivo = new ArchivoEntity();
        archivo.setNombreOriginal(nombreOriginal);
        archivo.setRutaServidor(archivoFisico.getAbsolutePath());
        archivo.setFechaSubida(LocalDateTime.now());
        archivo.setPaciente(paciente);

        try {
            return archivoPersistenciaIntegration.guardarRutaArchivo(archivo);
        } catch (Exception e) {
            throw new IOException("Error al registrar la ruta del archivo en la base de datos.", e);
        }
    }

    @Override
    public ArchivoEntity buscarPorId(Long idArchivo) throws IOException {
        try {
            return archivoPersistenciaIntegration.buscarPorId(idArchivo);
        } catch (Exception e) {
            throw new IOException("Error al buscar el archivo.", e);
        }
    }

    @Override
    public List<ArchivoEntity> listarPorPaciente(int idPaciente) throws IOException {
        try {
            return archivoPersistenciaIntegration.listarPorPaciente(idPaciente);
        } catch (Exception e) {
            throw new IOException("Error al listar archivos del paciente.", e);
        }
    }

    private String limpiarNombreArchivo(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "Paciente";
        }

        String limpio = Normalizer.normalize(texto, Normalizer.Form.NFD);
        limpio = limpio.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        limpio = limpio.replaceAll("[^a-zA-Z0-9]", "_");

        return limpio;
    }
}
