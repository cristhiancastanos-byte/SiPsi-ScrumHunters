package mx.sipsi.ui;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.sipsi.entity.ArchivoEntity;
import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.delegate.ArchivoDelegate;
import org.primefaces.event.FileUploadEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

@Named("archivoBean")
@ViewScoped
public class ArchivoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArchivoDelegate archivoDelegate;
    private ArchivoEntity archivoVista;
    private ArchivoEntity archivoEliminar;
    private Long idArchivoEliminar;

    @Inject
    private PacienteBean pacienteBean;

    public ArchivoBean() {
        this.archivoDelegate = new ArchivoDelegate();
    }

    public void handleFileUpload(FileUploadEvent event) {
        PacienteEntity pacienteActual = obtenerPacienteActual(event);

        try {
            if (pacienteActual == null || pacienteActual.getId() <= 0) {
                agregarError("Error", "No se encontró el paciente actual.");
                return;
            }

            if (event == null || event.getFile() == null) {
                agregarError("Error", "No se recibió ningún archivo.");
                return;
            }

            if (event.getFile().getSize() > 5242880) {
                agregarError("Archivo demasiado grande", "El archivo no debe superar 5MB.");
                return;
            }

            String nombreOriginal = event.getFile().getFileName();

            if (nombreOriginal == null || nombreOriginal.trim().isEmpty()) {
                agregarError("Error", "El nombre del archivo no es válido.");
                return;
            }

            String nombreLower = nombreOriginal.toLowerCase();

            if (!(nombreLower.endsWith(".jpg") || nombreLower.endsWith(".jpeg") || nombreLower.endsWith(".png"))) {
                agregarError("Formato no válido", "Solo se permiten imágenes JPG o PNG.");
                return;
            }

            ArchivoEntity archivoSubido;

            try (InputStream inputStream = event.getFile().getInputStream()) {
                archivoSubido = archivoDelegate.subirArchivo(inputStream, nombreOriginal, pacienteActual);
            }

            if (pacienteActual.getArchivos() == null) {
                pacienteActual.setArchivos(new ArrayList<>());
            }

            if (archivoSubido != null) {
                pacienteActual.getArchivos().add(0, archivoSubido);
            }

            agregarInfo("Archivo agregado correctamente", "El archivo se subió al expediente del paciente.");

        } catch (IOException e) {
            agregarError("No se pudo subir el archivo", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            agregarError("Error", "Ocurrió un problema al subir el archivo.");
        }
    }

    private PacienteEntity obtenerPacienteActual(FileUploadEvent event) {
        try {
            if (pacienteBean != null && pacienteBean.getPacienteExpediente() != null) {
                return pacienteBean.getPacienteExpediente();
            }
        } catch (Exception ignored) {
        }

        try {
            if (event != null && event.getComponent() != null) {
                Object pacienteObj = event.getComponent().getAttributes().get("pacienteActual");

                if (pacienteObj instanceof PacienteEntity) {
                    return (PacienteEntity) pacienteObj;
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public void prepararVista(Long idArchivo) {
        try {
            if (idArchivo == null) {
                agregarError("Error", "No se encontró el archivo seleccionado.");
                return;
            }

            archivoVista = archivoDelegate.buscarPorId(idArchivo);

            if (archivoVista == null) {
                agregarError("Error", "No se encontró el archivo seleccionado.");
            }

        } catch (IOException e) {
            agregarError("No se pudo abrir el archivo", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            agregarError("Error", "Ocurrió un problema al abrir el archivo.");
        }
    }

    public void prepararEliminarArchivo(Long idArchivo) {
        try {
            this.idArchivoEliminar = idArchivo;
            this.archivoEliminar = null;

            if (idArchivo == null) {
                agregarError("Error", "No se encontró el archivo seleccionado.");
                return;
            }

            this.archivoEliminar = archivoDelegate.buscarPorId(idArchivo);

            if (this.archivoEliminar == null) {
                agregarError("Error", "No se encontró el archivo seleccionado.");
            }

        } catch (IOException e) {
            agregarError("No se pudo preparar la eliminación", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            agregarError("Error", "Ocurrió un problema al preparar la eliminación.");
        }
    }

    public void confirmarEliminarArchivo() {
        eliminarArchivo(idArchivoEliminar);
        limpiarEliminacion();
    }

    public void eliminarArchivo(Long idArchivo) {
        try {
            if (idArchivo == null) {
                agregarError("Error", "No se encontró el archivo seleccionado.");
                return;
            }

            archivoDelegate.eliminarArchivo(idArchivo);

            PacienteEntity pacienteActual = null;

            if (pacienteBean != null) {
                pacienteActual = pacienteBean.getPacienteExpediente();
            }

            if (pacienteActual != null && pacienteActual.getArchivos() != null) {
                pacienteActual.getArchivos().removeIf(archivo -> archivo.getId() != null && archivo.getId().equals(idArchivo));
            }

            if (archivoVista != null && archivoVista.getId() != null && archivoVista.getId().equals(idArchivo)) {
                archivoVista = null;
            }

            agregarInfo("Archivo eliminado correctamente", "El archivo fue eliminado del expediente.");

        } catch (IOException e) {
            agregarError("No se pudo eliminar el archivo", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            agregarError("Error", "Ocurrió un problema al eliminar el archivo.");
        }
    }

    private void limpiarEliminacion() {
        this.idArchivoEliminar = null;
        this.archivoEliminar = null;
    }

    private void agregarInfo(String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, resumen, detalle));
    }

    private void agregarError(String resumen, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, resumen, detalle));
    }

    public ArchivoEntity getArchivoVista() {
        return archivoVista;
    }

    public void setArchivoVista(ArchivoEntity archivoVista) {
        this.archivoVista = archivoVista;
    }

    public ArchivoEntity getArchivoEliminar() {
        return archivoEliminar;
    }

    public void setArchivoEliminar(ArchivoEntity archivoEliminar) {
        this.archivoEliminar = archivoEliminar;
    }

    public Long getIdArchivoEliminar() {
        return idArchivoEliminar;
    }

    public void setIdArchivoEliminar(Long idArchivoEliminar) {
        this.idArchivoEliminar = idArchivoEliminar;
    }
}
