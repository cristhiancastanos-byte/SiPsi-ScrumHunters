package mx.sipsi.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.sipsi.entity.CitaEntity;
import mx.sipsi.entity.ReporteEntity;
import mx.sipsi.negocio.delegate.ReporteDelegate;
import org.primefaces.PrimeFaces;

import java.io.Serializable;

@Named("reporteBean")
@ViewScoped
public class ReporteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private ReporteEntity reporteNuevo;
    private CitaEntity citaSeleccionada;
    private ReporteDelegate reporteDelegate;

    @PostConstruct
    public void init() {
        reporteNuevo = new ReporteEntity();
        reporteDelegate = new ReporteDelegate();
    }

    public void prepararReporteDesdeAgenda() {
        try {
            String idCitaParam = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestParameterMap()
                    .get("idCita");

            if (idCitaParam == null || idCitaParam.trim().isEmpty()) {
                PrimeFaces.current().ajax().addCallbackParam("abrirReporte", false);
                return;
            }

            Integer idCita = Integer.parseInt(idCitaParam);

            if (reporteDelegate.existeReportePorCita(idCita)) {
                PrimeFaces.current().ajax().addCallbackParam("abrirReporte", false);
                return;
            }

            this.citaSeleccionada = new CitaEntity();
            this.citaSeleccionada.setIdCita(idCita);

            this.reporteNuevo = new ReporteEntity();
            this.reporteNuevo.setIdCita(idCita);

            PrimeFaces.current().ajax().addCallbackParam("abrirReporte", true);

        } catch (Exception e) {
            PrimeFaces.current().ajax().addCallbackParam("abrirReporte", false);
        }
    }

    public void prepararReporte(CitaEntity cita) {
        try {
            if (cita == null || cita.getIdCita() == null) {
                PrimeFaces.current().ajax().addCallbackParam("abrirReporte", false);
                return;
            }

            if (reporteDelegate.existeReportePorCita(cita.getIdCita())) {
                PrimeFaces.current().ajax().addCallbackParam("abrirReporte", false);
                return;
            }

            this.citaSeleccionada = cita;
            this.reporteNuevo = new ReporteEntity();
            this.reporteNuevo.setIdCita(cita.getIdCita());

            PrimeFaces.current().ajax().addCallbackParam("abrirReporte", true);

        } catch (Exception e) {
            PrimeFaces.current().ajax().addCallbackParam("abrirReporte", false);
        }
    }

    public void guardarReporte() {
        try {
            if (citaSeleccionada == null || citaSeleccionada.getIdCita() == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se seleccionó una cita válida.");
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }

            if (reporteNuevo == null) {
                reporteNuevo = new ReporteEntity();
            }

            reporteNuevo.setIdCita(citaSeleccionada.getIdCita());

            reporteDelegate.crearReporte(reporteNuevo);

            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Reporte guardado con éxito");

            limpiarReporte();

            PrimeFaces.current().executeScript("PF('modalReporte').hide();");

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    public void limpiarReporte() {
        this.reporteNuevo = new ReporteEntity();
        this.citaSeleccionada = null;
    }

    private void mostrarMensaje(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public ReporteEntity getReporteNuevo() {
        return reporteNuevo;
    }

    public void setReporteNuevo(ReporteEntity reporteNuevo) {
        this.reporteNuevo = reporteNuevo;
    }

    public CitaEntity getCitaSeleccionada() {
        return citaSeleccionada;
    }

    public void setCitaSeleccionada(CitaEntity citaSeleccionada) {
        this.citaSeleccionada = citaSeleccionada;
    }
}