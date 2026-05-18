package mx.sipsi.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.sipsi.entity.CitaEntity;
import mx.sipsi.entity.ImagenReporteEntity;
import mx.sipsi.entity.ReporteEntity;
import mx.sipsi.negocio.delegate.ImagenReporteDelegate;
import mx.sipsi.negocio.delegate.ReporteDelegate;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("reporteBean")
@ViewScoped
public class ReporteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private ReporteEntity reporteNuevo;
    private ReporteEntity reporteSeleccionado;
    private ReporteEntity reporteEditar;
    private CitaEntity citaSeleccionada;
    private ImagenReporteEntity imagenEliminar;

    private ReporteDelegate reporteDelegate;
    private ImagenReporteDelegate imagenReporteDelegate;

    private boolean mostrarDetalles;
    private boolean editandoReporte;

    private String motivoReporteSeleccionado;
    private String nombrePacienteReporte;
    private List<ImagenReporteEntity> imagenesReporteSeleccionado;

    @Inject
    private ImagenReporteBean imagenReporteBean;

    @PostConstruct
    public void init() {
        reporteNuevo = new ReporteEntity();
        reporteSeleccionado = null;
        reporteEditar = null;
        citaSeleccionada = null;
        imagenEliminar = null;

        reporteDelegate = new ReporteDelegate();
        imagenReporteDelegate = new ImagenReporteDelegate();

        mostrarDetalles = false;
        editandoReporte = false;

        motivoReporteSeleccionado = "";
        nombrePacienteReporte = "Paciente";
        imagenesReporteSeleccionado = new ArrayList<>();
    }

    public void prepararReporteDesdeAgenda() {
        try {
            String idCitaParam = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestParameterMap()
                    .get("idCita");

            String motivoCitaParam = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestParameterMap()
                    .get("motivoCita");

            String nombrePacienteParam = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestParameterMap()
                    .get("nombrePaciente");

            PrimeFaces.current().ajax().addCallbackParam("abrirCrearReporte", false);
            PrimeFaces.current().ajax().addCallbackParam("abrirVerReporte", false);

            if (idCitaParam == null || idCitaParam.trim().isEmpty()) {
                return;
            }

            Integer idCita = Integer.parseInt(idCitaParam);

            if (motivoCitaParam != null && !motivoCitaParam.trim().isEmpty()) {
                this.motivoReporteSeleccionado = limpiarTextoMotivo(motivoCitaParam);
            } else {
                this.motivoReporteSeleccionado = "Sin motivo registrado";
            }

            if (nombrePacienteParam != null && !nombrePacienteParam.trim().isEmpty()) {
                this.nombrePacienteReporte = nombrePacienteParam.trim();
            } else {
                this.nombrePacienteReporte = "Paciente";
            }

            if (reporteDelegate.existeReportePorCita(idCita)) {
                ReporteEntity reporteExistente = reporteDelegate.consultarReportePorCita(idCita);
                abrirReporte(reporteExistente.getIdReporte());
                PrimeFaces.current().ajax().addCallbackParam("abrirVerReporte", true);
                return;
            }

            this.citaSeleccionada = new CitaEntity();
            this.citaSeleccionada.setIdCita(idCita);
            this.citaSeleccionada.setMotivo(this.motivoReporteSeleccionado);

            this.reporteNuevo = new ReporteEntity();
            this.reporteNuevo.setIdCita(idCita);

            if (imagenReporteBean != null) {
                imagenReporteBean.prepararCargaImagen(this.reporteNuevo);
            }

            PrimeFaces.current().ajax().addCallbackParam("abrirCrearReporte", true);

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo preparar el reporte.");
            PrimeFaces.current().ajax().addCallbackParam("abrirCrearReporte", false);
            PrimeFaces.current().ajax().addCallbackParam("abrirVerReporte", false);
        }
    }

    public void prepararReporte(CitaEntity cita) {
        try {
            PrimeFaces.current().ajax().addCallbackParam("abrirCrearReporte", false);
            PrimeFaces.current().ajax().addCallbackParam("abrirVerReporte", false);

            if (cita == null || cita.getIdCita() == null) {
                return;
            }

            if (cita.getMotivo() != null && !cita.getMotivo().trim().isEmpty()) {
                this.motivoReporteSeleccionado = limpiarTextoMotivo(cita.getMotivo());
            } else {
                this.motivoReporteSeleccionado = "Sin motivo registrado";
            }

            if (reporteDelegate.existeReportePorCita(cita.getIdCita())) {
                ReporteEntity reporteExistente = reporteDelegate.consultarReportePorCita(cita.getIdCita());
                abrirReporte(reporteExistente.getIdReporte());
                PrimeFaces.current().ajax().addCallbackParam("abrirVerReporte", true);
                return;
            }

            this.citaSeleccionada = cita;
            this.reporteNuevo = new ReporteEntity();
            this.reporteNuevo.setIdCita(cita.getIdCita());

            if (imagenReporteBean != null) {
                imagenReporteBean.prepararCargaImagen(this.reporteNuevo);
            }

            PrimeFaces.current().ajax().addCallbackParam("abrirCrearReporte", true);

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo preparar el reporte.");
            PrimeFaces.current().ajax().addCallbackParam("abrirCrearReporte", false);
            PrimeFaces.current().ajax().addCallbackParam("abrirVerReporte", false);
        }
    }

    public void abrirReporte(Integer idReporte) {
        try {
            this.reporteSeleccionado = reporteDelegate.consultarReportePorId(idReporte);
            this.reporteEditar = null;
            this.imagenEliminar = null;
            this.mostrarDetalles = false;
            this.editandoReporte = false;

            if (imagenReporteBean != null) {
                imagenReporteBean.limpiarImagenesPendientes();
            }

            cargarImagenesReporteSeleccionado();

        } catch (Exception e) {
            this.reporteSeleccionado = null;
            this.reporteEditar = null;
            this.imagenEliminar = null;
            this.imagenesReporteSeleccionado = new ArrayList<>();
            this.mostrarDetalles = false;
            this.editandoReporte = false;

            if (imagenReporteBean != null) {
                imagenReporteBean.limpiarImagenesPendientes();
            }

            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void verDetalles() {
        if (editandoReporte) {
            return;
        }

        this.mostrarDetalles = !this.mostrarDetalles;
    }

    public void alternarEdicionReporte() {
        if (editandoReporte) {
            cancelarEdicionReporte();
            return;
        }

        if (!mostrarDetalles) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Aviso", "Primero muestra los detalles del reporte.");
            return;
        }

        prepararEdicionReporte(reporteSeleccionado);
    }

    public void prepararEdicionReporte(ReporteEntity reporte) {
        try {
            if (reporte == null || reporte.getIdReporte() == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se seleccionó un reporte válido.");
                return;
            }

            ReporteEntity reporteActual = reporteDelegate.consultarReportePorId(reporte.getIdReporte());

            this.reporteEditar = new ReporteEntity();
            this.reporteEditar.setIdReporte(reporteActual.getIdReporte());
            this.reporteEditar.setIdCita(reporteActual.getIdCita());
            this.reporteEditar.setContenido(reporteActual.getContenido());
            this.reporteEditar.setFechaCreacion(reporteActual.getFechaCreacion());

            this.editandoReporte = true;
            this.mostrarDetalles = true;

            if (imagenReporteBean != null) {
                imagenReporteBean.prepararCargaImagen(this.reporteEditar);
            }

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo cargar el reporte para editar.");
        }
    }

    public void actualizarReporte() {
        try {
            if (reporteEditar == null || reporteEditar.getIdReporte() == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se seleccionó un reporte válido.");
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }

            if (reporteEditar.getContenido() == null || reporteEditar.getContenido().trim().isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "La nota de sesión no puede quedar vacía.");
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }

            Integer idReporte = reporteEditar.getIdReporte();

            reporteEditar.setContenido(reporteEditar.getContenido().trim());

            reporteDelegate.actualizarReporte(reporteEditar);

            ReporteEntity reporteActualizado = reporteDelegate.consultarReportePorId(idReporte);

            if (imagenReporteBean != null) {
                imagenReporteBean.guardarImagenesPendientes(reporteActualizado);
            }

            this.reporteSeleccionado = reporteDelegate.consultarReportePorId(idReporte);
            this.reporteEditar = null;
            this.imagenEliminar = null;
            this.editandoReporte = false;
            this.mostrarDetalles = true;

            cargarImagenesReporteSeleccionado();

            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Reporte actualizado");

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    public void cancelarEdicionReporte() {
        this.reporteEditar = null;
        this.imagenEliminar = null;
        this.editandoReporte = false;
        this.mostrarDetalles = true;

        if (imagenReporteBean != null) {
            imagenReporteBean.limpiarImagenesPendientes();
        }
    }

    public void prepararEliminacionImagenReporte(ImagenReporteEntity imagen) {
        this.imagenEliminar = imagen;
    }

    public void eliminarImagenReporte() {
        try {
            if (imagenEliminar == null || imagenEliminar.getIdImagenReporte() == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se seleccionó un archivo válido.");
                return;
            }

            imagenReporteDelegate.eliminarImagen(imagenEliminar.getIdImagenReporte());

            this.imagenEliminar = null;

            cargarImagenesReporteSeleccionado();

            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Archivo eliminado");

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el archivo.");
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

            if (imagenReporteBean != null) {
                imagenReporteBean.guardarImagenesPendientes(reporteNuevo);
            }

            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Reporte guardado con éxito");

            limpiarReporte();

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    public void limpiarReporte() {
        this.reporteNuevo = new ReporteEntity();
        this.reporteSeleccionado = null;
        this.reporteEditar = null;
        this.citaSeleccionada = null;
        this.imagenEliminar = null;
        this.mostrarDetalles = false;
        this.editandoReporte = false;
        this.motivoReporteSeleccionado = "";
        this.nombrePacienteReporte = "Paciente";
        this.imagenesReporteSeleccionado = new ArrayList<>();

        if (imagenReporteBean != null) {
            imagenReporteBean.limpiarImagenesPendientes();
        }
    }

    private void cargarImagenesReporteSeleccionado() throws Exception {
        if (this.reporteSeleccionado != null && this.reporteSeleccionado.getIdReporte() != null) {
            this.imagenesReporteSeleccionado = imagenReporteDelegate.listarImagenesPorReporte(
                    this.reporteSeleccionado.getIdReporte()
            );
        } else {
            this.imagenesReporteSeleccionado = new ArrayList<>();
        }
    }

    private String limpiarTextoMotivo(String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            return "Sin motivo registrado";
        }

        String motivoLimpio = motivo.trim();

        if (motivoLimpio.startsWith("Motivo:")) {
            motivoLimpio = motivoLimpio.substring("Motivo:".length()).trim();
        }

        return motivoLimpio.isEmpty() ? "Sin motivo registrado" : motivoLimpio;
    }

    private void mostrarMensaje(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public String getTextoBotonDetalles() {
        return mostrarDetalles ? "Ocultar detalles" : "Ver detalles";
    }

    public String getTextoTooltipEditar() {
        if (!mostrarDetalles) {
            return "Muestra los detalles para editar";
        }

        return editandoReporte ? "Cancelar edición" : "Editar reporte";
    }

    public ReporteEntity getReporteNuevo() {
        return reporteNuevo;
    }

    public void setReporteNuevo(ReporteEntity reporteNuevo) {
        this.reporteNuevo = reporteNuevo;
    }

    public ReporteEntity getReporteSeleccionado() {
        return reporteSeleccionado;
    }

    public void setReporteSeleccionado(ReporteEntity reporteSeleccionado) {
        this.reporteSeleccionado = reporteSeleccionado;
    }

    public ReporteEntity getReporteEditar() {
        return reporteEditar;
    }

    public void setReporteEditar(ReporteEntity reporteEditar) {
        this.reporteEditar = reporteEditar;
    }

    public CitaEntity getCitaSeleccionada() {
        return citaSeleccionada;
    }

    public void setCitaSeleccionada(CitaEntity citaSeleccionada) {
        this.citaSeleccionada = citaSeleccionada;
    }

    public ImagenReporteEntity getImagenEliminar() {
        return imagenEliminar;
    }

    public void setImagenEliminar(ImagenReporteEntity imagenEliminar) {
        this.imagenEliminar = imagenEliminar;
    }

    public boolean isMostrarDetalles() {
        return mostrarDetalles;
    }

    public void setMostrarDetalles(boolean mostrarDetalles) {
        this.mostrarDetalles = mostrarDetalles;
    }

    public boolean isEditandoReporte() {
        return editandoReporte;
    }

    public void setEditandoReporte(boolean editandoReporte) {
        this.editandoReporte = editandoReporte;
    }

    public String getMotivoReporteSeleccionado() {
        return motivoReporteSeleccionado;
    }

    public void setMotivoReporteSeleccionado(String motivoReporteSeleccionado) {
        this.motivoReporteSeleccionado = motivoReporteSeleccionado;
    }

    public String getNombrePacienteReporte() {
        return nombrePacienteReporte;
    }

    public void setNombrePacienteReporte(String nombrePacienteReporte) {
        this.nombrePacienteReporte = nombrePacienteReporte;
    }

    public List<ImagenReporteEntity> getImagenesReporteSeleccionado() {
        return imagenesReporteSeleccionado;
    }

    public void setImagenesReporteSeleccionado(List<ImagenReporteEntity> imagenesReporteSeleccionado) {
        this.imagenesReporteSeleccionado = imagenesReporteSeleccionado;
    }
}
