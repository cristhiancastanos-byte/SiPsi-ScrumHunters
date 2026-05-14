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
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
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
    private CitaHelper helper;
    private PacienteDelegate pacienteDelegate;
    private List<CitaEntity> citasAgendadas;

    private Date horaInicioAux;
    private Date horaFinAux;

    private boolean formIntentado = false;

    private ScheduleModel eventModel;

    public CitaBean() {
        this.citaNueva = new CitaEntity();
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

                    String claseEvento;

                    if (esCitaCancelada(cita)) {
                        claseEvento = "evento-cancelado";
                    } else if (esCitaRealizada(cita)) {
                        claseEvento = "evento-activo evento-realizada";
                    } else {
                        claseEvento = "evento-activo";
                    }

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

    private boolean esCitaRealizada(CitaEntity cita) {
        if (cita == null || cita.getFecha() == null || cita.getHoraFin() == null) {
            return false;
        }

        if (esCitaCancelada(cita)) {
            return false;
        }

        LocalDateTime fechaHoraFin = convertirALocalDateTime(cita.getFecha(), cita.getHoraFin());

        if (fechaHoraFin == null) {
            return false;
        }

        return fechaHoraFin.isBefore(LocalDateTime.now());
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

    public CitaEntity getCitaNueva() {
        return citaNueva;
    }

    public void setCitaNueva(CitaEntity citaNueva) {
        this.citaNueva = citaNueva;
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
}