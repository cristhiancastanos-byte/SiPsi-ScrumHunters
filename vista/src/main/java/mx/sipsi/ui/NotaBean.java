package mx.sipsi.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import mx.sipsi.entity.NotaEntity;
import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.helper.NotaHelper;
import mx.sipsi.negocio.delegate.NotaDelegate;
import org.primefaces.PrimeFaces;

@Named("notaBean")
@ViewScoped
public class NotaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private NotaEntity nuevaNota;

    private NotaHelper notaHelper;
    private NotaDelegate notaDelegate;

    @Inject
    private PacienteBean pacienteBean;

    @PostConstruct
    public void init() {
        nuevaNota = new NotaEntity();
        notaHelper = new NotaHelper();
        notaDelegate = new NotaDelegate();
    }

    public void guardarNota(ActionEvent event) {
        try {
            PacienteEntity pacienteActual = pacienteBean.getPacienteExpediente();

            if (!notaHelper.validarContenidoNota(nuevaNota.getContenido())) {
                FacesContext.getCurrentInstance().validationFailed();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Campo Obligatorio",
                                "La nota clínica no puede estar vacía."));
                return;
            }

            if (pacienteActual == null) {
                FacesContext.getCurrentInstance().validationFailed();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error",
                                "No se encontró el paciente asociado a la nota."));
                return;
            }

            nuevaNota.setPaciente(pacienteActual);

            notaDelegate.agregarNota(nuevaNota);

            nuevaNota = new NotaEntity();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Nota agregada correctamente",
                            "La nota clínica se guardó correctamente."));

            PrimeFaces.current().ajax().update("frmExpediente:msgsExpediente");
            PrimeFaces.current().ajax().update("frmExpediente:pnlExpediente");
            PrimeFaces.current().executeScript("document.getElementById('frmExpediente:formNuevaNota').style.display='none';");

        } catch (Exception e) {
            e.printStackTrace();

            FacesContext.getCurrentInstance().validationFailed();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al guardar",
                            e.getMessage()));
        }
    }

    public NotaEntity getNuevaNota() {
        return nuevaNota;
    }

    public void setNuevaNota(NotaEntity nuevaNota) {
        this.nuevaNota = nuevaNota;
    }
}