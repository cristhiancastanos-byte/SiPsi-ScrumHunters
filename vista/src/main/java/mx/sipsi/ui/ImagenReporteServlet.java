package mx.sipsi.ui;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.sipsi.entity.ImagenReporteEntity;
import mx.sipsi.negocio.delegate.ImagenReporteDelegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/imagenReporte")
public class ImagenReporteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ImagenReporteDelegate imagenReporteDelegate;

    @Override
    public void init() throws ServletException {
        this.imagenReporteDelegate = new ImagenReporteDelegate();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No se recibió la imagen.");
                return;
            }

            Integer idImagen = Integer.parseInt(idParam);

            ImagenReporteEntity imagen = imagenReporteDelegate.consultarImagenPorId(idImagen);

            if (imagen == null || imagen.getRutaServidor() == null || imagen.getRutaServidor().trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No se encontró la imagen.");
                return;
            }

            File archivo = new File(imagen.getRutaServidor());

            if (!archivo.exists() || !archivo.isFile()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "El archivo físico no existe.");
                return;
            }

            String nombre = imagen.getNombreOriginal();

            if (nombre == null) {
                nombre = "imagen";
            }

            String nombreLower = nombre.toLowerCase();

            if (nombreLower.endsWith(".jpg") || nombreLower.endsWith(".jpeg")) {
                response.setContentType("image/jpeg");
            } else if (nombreLower.endsWith(".png")) {
                response.setContentType("image/png");
            } else {
                response.setContentType("application/octet-stream");
            }

            response.setHeader("Content-Disposition", "inline; filename=\"" + nombre + "\"");
            response.setContentLengthLong(archivo.length());

            try (FileInputStream inputStream = new FileInputStream(archivo);
                 OutputStream outputStream = response.getOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesLeidos;

                while ((bytesLeidos = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesLeidos);
                }
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El identificador de la imagen no es válido.");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo cargar la imagen.");
        }
    }
}