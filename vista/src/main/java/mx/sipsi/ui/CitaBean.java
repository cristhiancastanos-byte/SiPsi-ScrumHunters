package mx.sipsi.ui;

import mx.sipsi.entity.CitaEntity;
import mx.sipsi.helper.CitaHelper;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("citaBean")
@ViewScoped
public class CitaBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private CitaEntity citaNueva;
    private CitaHelper helper;
    private List<CitaEntity> citasAgendadas;

    private Date horaInicioAux;
    private Date horaFinAux;

    private boolean formIntentado = false;

    public CitaBean() {
        this.citaNueva = new CitaEntity();
        this.helper = new CitaHelper();
        this.citasAgendadas = new ArrayList<>();
    }

    public void guardar() {
        this.formIntentado = true;

        if (horaInicioAux != null) {
            citaNueva.setHoraInicio(new Time(horaInicioAux.getTime()));
        }
        if (horaFinAux != null) {
            citaNueva.setHoraFin(new Time(horaFinAux.getTime()));
        }

        if (horaInicioAux != null && horaFinAux != null) {
            long diff = horaFinAux.getTime() - horaInicioAux.getTime();
            long minutos = diff / (60 * 1000);

            if (minutos <= 0 || minutos > 180) {
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
        }

        if (!helper.validarDatos(citaNueva)) {
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        boolean hayEmpalme = helper.validarEmpalmeHorario(citaNueva.getFecha(), citaNueva.getHoraInicio());
        if (hayEmpalme) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Horario no disponible", "Ya existe una cita en esa fecha y hora.");
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        try {
            helper.getCitaDelegate().registrarCita(citaNueva);
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "La cita se ha registrado correctamente.");
            limpiarFormulario();
            cargarAgenda();
        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_FATAL, "Error", "Problema al guardar: " + e.getMessage());
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    public void limpiarFormulario() {
        this.citaNueva = new CitaEntity();
        this.horaInicioAux = null;
        this.horaFinAux = null;
        this.formIntentado = false;
    }

    public void cargarAgenda() {
    }

    private void mostrarMensaje(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public Date getHoraInicioAux() { return horaInicioAux; }
    public void setHoraInicioAux(Date horaInicioAux) { this.horaInicioAux = horaInicioAux; }

    public Date getHoraFinAux() { return horaFinAux; }
    public void setHoraFinAux(Date horaFinAux) { this.horaFinAux = horaFinAux; }

    public CitaEntity getCitaNueva() { return citaNueva; }
    public void setCitaNueva(CitaEntity citaNueva) { this.citaNueva = citaNueva; }

    public List<CitaEntity> getCitasAgendadas() { return citasAgendadas; }
    public void setCitasAgendadas(List<CitaEntity> citasAgendadas) { this.citasAgendadas = citasAgendadas; }

    public boolean isFormIntentado() { return formIntentado; }
    public void setFormIntentado(boolean formIntentado) { this.formIntentado = formIntentado; }
}