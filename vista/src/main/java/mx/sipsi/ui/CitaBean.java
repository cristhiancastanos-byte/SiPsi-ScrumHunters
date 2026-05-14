package mx.sipsi.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.sipsi.entity.CitaEntity;
import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.helper.CitaHelper;
import mx.sipsi.negocio.delegate.PacienteDelegate;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("citaBean")
@ViewScoped
public class CitaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private CitaEntity citaNueva;
    private CitaEntity citaEditar;

    private String nombrePacienteEditar;
    private String citaOriginalTexto;
    private LocalDateTime fechaHoraOriginalEditar;

    private CitaHelper helper;
    private PacienteDelegate pacienteDelegate;
    private List<CitaEntity> citasAgendadas;

    private Date horaInicioAux;
    private Date horaFinAux;

    private Date horaInicioEditarAux;
    private Date horaFinEditarAux;

    private boolean formIntentado = false;
    private boolean formEdicionIntentado = false;

    private ScheduleModel eventModel;

    public CitaBean() {
        this.citaNueva = new CitaEntity();
        this.citaEditar = new CitaEntity();
        this.helper = new CitaHelper();
        this.pacienteDelegate = new PacienteDelegate();
        this.citasAgendadas = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        inicializarCalendario();
    }

    private void inicializarCalendario() {
        eventModel = new LazyScheduleModel() {
            @Override
            public void loadEvents(LocalDateTime inicio, LocalDateTime fin) {

                LocalDateTime fechaReferencia = inicio.plusDays(15);

                int mes = fechaReferencia.getMonthValue();
                int anio = fechaReferencia.getYear();

                List<CitaEntity> citas = helper.getCitaDelegate().consultarAgenda(mes, anio);
                Map<Integer, String> nombresPacientes = new HashMap<>();

                for (CitaEntity cita : citas) {
                    LocalDateTime fechaInicio = convertirALocalDateTime(cita.getFecha(), cita.getHoraInicio());
                    LocalDateTime fechaFin = convertirALocalDateTime(cita.getFecha(), cita.getHoraFin());

                    if (fechaInicio == null) {
                        continue;
                    }

                    if (fechaFin == null) {
                        fechaFin = fechaInicio.plusHours(1);
                    }

                    String nombrePaciente = obtenerNombreParaAgenda(cita, nombresPacientes);
                    String titulo = construirTituloEvento(cita, nombrePaciente);

                    String claseEvento = esCitaCancelada(cita)
                            ? "evento-cancelado"
                            : "evento-activo evento-editable";

                    DefaultScheduleEvent<?> evento = DefaultScheduleEvent.builder()
                            .id(String.valueOf(cita.getIdCita()))
                            .title(titulo)
                            .startDate(fechaInicio)
                            .endDate(fechaFin)
                            .data(cita)
                            .styleClass(claseEvento)
                            .build();

                    addEvent(evento);
                }
            }
        };
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
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Duración inválida", "La cita debe durar máximo 3 horas.");
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
        }

        if (!helper.validarDatos(citaNueva)) {
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        if (citaEsAntesDelMomentoActual(citaNueva)) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Fecha u horario inválido", "No se pueden agendar citas antes del día u horario actual.");
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        boolean hayEmpalme = helper.validarEmpalmeHorario(
                citaNueva.getFecha(),
                citaNueva.getHoraInicio(),
                citaNueva.getHoraFin()
        );

        if (hayEmpalme) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Horario no disponible", "Ya existe una cita registrada dentro de ese horario.");
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        try {
            copiarNombrePacienteHistorico();

            helper.getCitaDelegate().registrarCita(citaNueva);
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "La cita se ha registrado correctamente.");
            limpiarFormulario();
            cargarAgenda();

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_FATAL, "Error", "Problema al guardar: " + e.getMessage());
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    public void onEventSelect(SelectEvent<ScheduleEvent<?>> selectEvent) {
        ScheduleEvent<?> evento = selectEvent.getObject();

        if (evento != null && evento.getData() instanceof CitaEntity) {
            CitaEntity citaSeleccionada = (CitaEntity) evento.getData();
            prepararEdicionCita(citaSeleccionada);
            PrimeFaces.current().executeScript("PF('dlgEditarCita').show();");
        }
    }

    public void prepararEdicionDesdeAgenda() {
        try {
            String idCitaParam = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestParameterMap()
                    .get("idCita");

            if (idCitaParam == null || idCitaParam.trim().isEmpty()) {
                PrimeFaces.current().ajax().addCallbackParam("abrirEdicion", false);
                return;
            }

            Integer idCita = Integer.parseInt(idCitaParam);

            CitaEntity citaSeleccionada = new CitaEntity();
            citaSeleccionada.setIdCita(idCita);

            prepararEdicionCita(citaSeleccionada);

            if (FacesContext.getCurrentInstance().isValidationFailed()) {
                PrimeFaces.current().ajax().addCallbackParam("abrirEdicion", false);
                return;
            }

            PrimeFaces.current().ajax().addCallbackParam("abrirEdicion", true);

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo cargar la cita para editar.");
            FacesContext.getCurrentInstance().validationFailed();
            PrimeFaces.current().ajax().addCallbackParam("abrirEdicion", false);
        }
    }

    public void prepararEdicionCita(CitaEntity cita) {
        this.formEdicionIntentado = false;

        if (cita == null || cita.getIdCita() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Cita inválida", "No se pudo cargar la cita seleccionada.");
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        try {
            this.citaEditar = helper.getCitaDelegate().consultarCitaPorId(cita.getIdCita());

            this.nombrePacienteEditar = obtenerNombrePaciente(this.citaEditar.getIdPaciente(), new HashMap<>());
            this.citaOriginalTexto = construirTextoCitaOriginal(this.citaEditar);
            this.fechaHoraOriginalEditar = convertirALocalDateTime(this.citaEditar.getFecha(), this.citaEditar.getHoraInicio());

            if (this.citaEditar.getHoraInicio() != null) {
                this.horaInicioEditarAux = new Date(this.citaEditar.getHoraInicio().getTime());
            }

            if (this.citaEditar.getHoraFin() != null) {
                this.horaFinEditarAux = new Date(this.citaEditar.getHoraFin().getTime());
            }

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar los datos de la cita: " + e.getMessage());
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    public void actualizarCita() {
        this.formEdicionIntentado = true;

        if (citaEditar == null || citaEditar.getIdCita() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Cita inválida", "No se ha seleccionado una cita para reprogramar.");
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        if (horaInicioEditarAux != null) {
            citaEditar.setHoraInicio(new Time(horaInicioEditarAux.getTime()));
        }

        if (horaFinEditarAux != null) {
            citaEditar.setHoraFin(new Time(horaFinEditarAux.getTime()));
        }

        if (citaEditar.getFecha() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Campo obligatorio", "La fecha de la cita es obligatoria.");
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        if (citaEditar.getHoraInicio() == null || citaEditar.getHoraFin() == null) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Campo obligatorio", "La hora de inicio y la hora de fin son obligatorias.");
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        if (!citaEditar.getHoraInicio().before(citaEditar.getHoraFin())) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Horario inválido", "La hora de inicio debe ser menor que la hora de fin.");
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        long diff = citaEditar.getHoraFin().getTime() - citaEditar.getHoraInicio().getTime();
        long minutos = diff / (60 * 1000);

        if (minutos <= 0 || minutos > 180) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Duración inválida", "La cita debe durar máximo 3 horas.");
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        if (!cumpleAnticipacionMinima12Horas()) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Anticipación mínima", "Se requiere al menos 12 horas de anticipación para reprogramar.");
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        if (citaEsAntesDelMomentoActual(citaEditar)) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Fecha inválida", "No se puede reprogramar la cita a una fecha u horario que ya pasó.");
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        try {
            helper.getCitaDelegate().actualizarCita(citaEditar);

            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito", "Cita agendada con éxito.");
            limpiarFormularioEdicion();
            cargarAgenda();

            PrimeFaces.current().executeScript("PF('dlgEditarCita').hide();");

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Horario no disponible", e.getMessage());
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    private boolean cumpleAnticipacionMinima12Horas() {
        if (fechaHoraOriginalEditar == null) {
            return false;
        }

        return fechaHoraOriginalEditar.isAfter(LocalDateTime.now().plusHours(12));
    }

    private String construirTextoCitaOriginal(CitaEntity cita) {
        if (cita == null || cita.getFecha() == null || cita.getHoraInicio() == null) {
            return "";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cita.getFecha());

        String[] meses = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        String mes = meses[calendar.get(Calendar.MONTH)];
        int hora = cita.getHoraInicio().toLocalTime().getHour();

        return String.format("%02d/%s, %02d hrs", dia, mes, hora);
    }

    public void limpiarFormularioEdicion() {
        this.citaEditar = new CitaEntity();
        this.horaInicioEditarAux = null;
        this.horaFinEditarAux = null;
        this.nombrePacienteEditar = null;
        this.citaOriginalTexto = null;
        this.fechaHoraOriginalEditar = null;
        this.formEdicionIntentado = false;
    }

    private boolean citaEsAntesDelMomentoActual(CitaEntity cita) {
        LocalDateTime fechaHoraInicio = convertirALocalDateTime(cita.getFecha(), cita.getHoraInicio());

        if (fechaHoraInicio == null) {
            return true;
        }

        return fechaHoraInicio.isBefore(LocalDateTime.now());
    }

    private void copiarNombrePacienteHistorico() {
        if (citaNueva == null || citaNueva.getIdPaciente() == null || citaNueva.getIdPaciente() <= 0) {
            return;
        }

        try {
            PacienteEntity paciente = pacienteDelegate.consultarPorId(citaNueva.getIdPaciente());

            if (paciente != null && paciente.getNombre() != null && !paciente.getNombre().trim().isEmpty()) {
                citaNueva.setNombrePacienteHistorico(paciente.getNombre());
            }

        } catch (Exception e) {
            citaNueva.setNombrePacienteHistorico("Paciente no encontrado");
        }
    }

    private String obtenerNombreParaAgenda(CitaEntity cita, Map<Integer, String> nombresPacientes) {
        if (cita == null) {
            return "Paciente eliminado";
        }

        String nombreHistorico = cita.getNombrePacienteHistorico();

        if (nombreHistorico != null && !nombreHistorico.trim().isEmpty()) {
            return nombreHistorico;
        }

        return obtenerNombrePaciente(cita.getIdPaciente(), nombresPacientes);
    }

    public void limpiarFormulario() {
        this.citaNueva = new CitaEntity();
        this.horaInicioAux = null;
        this.horaFinAux = null;
        this.formIntentado = false;
    }

    public void cargarAgenda() {
        inicializarCalendario();
    }

    private LocalDateTime convertirALocalDateTime(Date fecha, Time hora) {
        if (fecha == null || hora == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);

        LocalDate localDate = LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        LocalTime localTime = hora.toLocalTime();

        return LocalDateTime.of(localDate, localTime);
    }

    private String obtenerNombrePaciente(Integer idPaciente, Map<Integer, String> nombresPacientes) {
        if (idPaciente == null || idPaciente <= 0) {
            return "Paciente eliminado";
        }

        if (nombresPacientes.containsKey(idPaciente)) {
            return nombresPacientes.get(idPaciente);
        }

        try {
            PacienteEntity paciente = pacienteDelegate.consultarPorId(idPaciente);

            String nombre = paciente != null && paciente.getNombre() != null
                    ? paciente.getNombre()
                    : "Paciente eliminado";

            nombresPacientes.put(idPaciente, nombre);
            return nombre;

        } catch (Exception e) {
            nombresPacientes.put(idPaciente, "Paciente eliminado");
            return "Paciente eliminado";
        }
    }

    private String construirTituloEvento(CitaEntity cita, String nombrePaciente) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        String horaInicio = cita.getHoraInicio() != null
                ? cita.getHoraInicio().toLocalTime().format(formatter)
                : "";

        String horaFin = cita.getHoraFin() != null
                ? cita.getHoraFin().toLocalTime().format(formatter)
                : "";

        return nombrePaciente + "\n" + horaInicio + " - " + horaFin;
    }

    private boolean esCitaCancelada(CitaEntity cita) {
        return cita.getEstado() != null && cita.getEstado().equalsIgnoreCase("CANCELADA");
    }

    private void mostrarMensaje(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public Date getHoraInicioAux() {
        return horaInicioAux;
    }

    public void setHoraInicioAux(Date horaInicioAux) {
        this.horaInicioAux = horaInicioAux;
    }

    public Date getHoraFinAux() {
        return horaFinAux;
    }

    public void setHoraFinAux(Date horaFinAux) {
        this.horaFinAux = horaFinAux;
    }

    public Date getHoraInicioEditarAux() {
        return horaInicioEditarAux;
    }

    public void setHoraInicioEditarAux(Date horaInicioEditarAux) {
        this.horaInicioEditarAux = horaInicioEditarAux;
    }

    public Date getHoraFinEditarAux() {
        return horaFinEditarAux;
    }

    public void setHoraFinEditarAux(Date horaFinEditarAux) {
        this.horaFinEditarAux = horaFinEditarAux;
    }

    public CitaEntity getCitaNueva() {
        return citaNueva;
    }

    public void setCitaNueva(CitaEntity citaNueva) {
        this.citaNueva = citaNueva;
    }

    public CitaEntity getCitaEditar() {
        return citaEditar;
    }

    public void setCitaEditar(CitaEntity citaEditar) {
        this.citaEditar = citaEditar;
    }

    public String getNombrePacienteEditar() {
        return nombrePacienteEditar;
    }

    public void setNombrePacienteEditar(String nombrePacienteEditar) {
        this.nombrePacienteEditar = nombrePacienteEditar;
    }

    public String getCitaOriginalTexto() {
        return citaOriginalTexto;
    }

    public void setCitaOriginalTexto(String citaOriginalTexto) {
        this.citaOriginalTexto = citaOriginalTexto;
    }

    public List<CitaEntity> getCitasAgendadas() {
        return citasAgendadas;
    }

    public void setCitasAgendadas(List<CitaEntity> citasAgendadas) {
        this.citasAgendadas = citasAgendadas;
    }

    public boolean isFormIntentado() {
        return formIntentado;
    }

    public void setFormIntentado(boolean formIntentado) {
        this.formIntentado = formIntentado;
    }

    public boolean isFormEdicionIntentado() {
        return formEdicionIntentado;
    }

    public void setFormEdicionIntentado(boolean formEdicionIntentado) {
        this.formEdicionIntentado = formEdicionIntentado;
    }
}