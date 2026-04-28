package mx.sipsi.ui;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.sipsi.entity.ArchivoEntity;
import mx.sipsi.negocio.delegate.ArchivoDelegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

@WebServlet("/archivo")
public class ArchivoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ArchivoDelegate archivoDelegate;

    @Override
    public void init() throws ServletException {
        this.archivoDelegate = new ArchivoDelegate();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Archivo no especificado.");
            return;
        }

        try {
            Long idArchivo = Long.valueOf(idParam);
            ArchivoEntity archivo = archivoDelegate.buscarPorId(idArchivo);

            if (archivo == null || archivo.getRutaServidor() == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado.");
                return;
            }

            File archivoFisico = new File(archivo.getRutaServidor());

            if (!archivoFisico.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo físico no encontrado.");
                return;
            }

            String contentType = Files.probeContentType(archivoFisico.toPath());

            if (contentType == null) {
                String nombre = archivo.getNombreOriginal() != null
                        ? archivo.getNombreOriginal().toLowerCase()
                        : "";

                if (nombre.endsWith(".png")) {
                    contentType = "image/png";
                } else {
                    contentType = "image/jpeg";
                }
            }

            response.reset();
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline; filename=\"" + archivo.getNombreOriginal() + "\"");
            response.setContentLengthLong(archivoFisico.length());

            try (FileInputStream input = new FileInputStream(archivoFisico);
                 OutputStream output = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesLeidos;

                while ((bytesLeidos = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesLeidos);
                }

                output.flush();
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Id de archivo inválido.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo mostrar el archivo.");
        }
    }
}
