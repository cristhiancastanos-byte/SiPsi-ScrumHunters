package mx.sipsi.ui;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.delegate.ArchivoDelegate;
import org.primefaces.event.FileUploadEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

@Named("archivoBean")
@ViewScoped
public class ArchivoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArchivoDelegate archivoDelegate;

    public ArchivoBean() {
        this.archivoDelegate = new ArchivoDelegate();
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            PacienteEntity pacienteActual = (PacienteEntity) event.getComponent()
                    .getAttributes()
                    .get("pacienteActual");

            if (pacienteActual == null || pacienteActual.getId() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error",
                                "No se encontró el paciente actual."));
                return;
            }

            String nombreOriginal = event.getFile().getFileName();
            InputStream inputStream = event.getFile().getInputStream();

            archivoDelegate.subirArchivo(inputStream, nombreOriginal, pacienteActual);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Archivo agregado correctamente",
                            "El archivo se subió al expediente del paciente."));

        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "No se pudo subir el archivo",
                            "Verifica el formato o tamaño."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "Ocurrió un problema al subir el archivo."));
        }
    }
}