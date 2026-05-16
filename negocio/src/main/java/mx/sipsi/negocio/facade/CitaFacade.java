package mx.sipsi.negocio.facade;

import mx.sipsi.entity.CitaEntity;
import mx.sipsi.persistence.dao.CitaDAO;
import mx.sipsi.persistence.integration.ICitaPersistenciaIntegration;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CitaFacade {

    private ICitaPersistenciaIntegration persistencia;

    public CitaFacade() {
        this.persistencia = new CitaDAO();
    }

    public void procesarRegistro(CitaEntity cita) {
        if (cita == null) {
            throw new IllegalArgumentException("La cita no puede estar vacía");
        }

        if (cita.getFecha() == null) {
            throw new IllegalArgumentException("La fecha de la cita es obligatoria");
        }

        if (cita.getHoraInicio() == null || cita.getHoraFin() == null) {
            throw new IllegalArgumentException("La hora de inicio y fin son obligatorias");
        }

        if (!cita.getHoraInicio().before(cita.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio debe ser menor que la hora de fin");
        }

        boolean existeTraslape = procesarBusquedaEmpalme(
                cita.getFecha(),
                cita.getHoraInicio(),
                cita.getHoraFin()
        );

        if (existeTraslape) {
            throw new IllegalArgumentException("Ya existe una cita registrada en ese horario");
        }

        persistencia.save(cita);
    }

    public boolean procesarBusquedaEmpalme(Date fecha, Time horaInicio, Time horaFin) {
        if (fecha == null || horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException("Fecha, hora de inicio y hora de fin son obligatorias");
        }

        if (!horaInicio.before(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser menor que la hora de fin");
        }

        return persistencia.existeTraslape(fecha, horaInicio, horaFin);
    }

    public List<CitaEntity> consultarAgenda(int mes, int anio) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mes inválido para consultar la agenda");
        }

        if (anio <= 0) {
            throw new IllegalArgumentException("Año inválido para consultar la agenda");
        }

        return persistencia.obtenerCitasPorMes(mes, anio);
    }

    public boolean procesarValidacionCitasPendientes(int idPaciente) {
        if (idPaciente <= 0) {
            throw new IllegalArgumentException("Paciente inválido");
        }

        return persistencia.tieneCitasPendientesPorPaciente(idPaciente);
    }

    public void procesarEliminacionCitasPendientes(int idPaciente) {
        if (idPaciente <= 0) {
            throw new IllegalArgumentException("Paciente inválido");
        }

        persistencia.eliminarCitasPendientesPorPaciente(idPaciente);
    }

    public CitaEntity consultarCitaPorId(Integer idCita) {
        if (idCita == null || idCita <= 0) {
            throw new IllegalArgumentException("La cita seleccionada no es válida");
        }

        CitaEntity cita = persistencia.consultarCitaPorId(idCita);

        if (cita == null) {
            throw new IllegalArgumentException("No se encontró la cita seleccionada");
        }

        return cita;
    }

    public void cancelarCita(Integer idCita, String motivo) {
        if (idCita == null || idCita <= 0) {
            throw new IllegalArgumentException("La cita seleccionada no es válida");
        }

        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obligatorio");
        }

        CitaEntity cita = consultarCitaPorId(idCita);

        if (cita == null) {
            throw new IllegalArgumentException("No se encontró la cita seleccionada");
        }

        if ("Cancelada".equalsIgnoreCase(cita.getEstado())) {
            throw new IllegalArgumentException("La cita ya se encuentra cancelada");
        }

        persistencia.cancelarCita(idCita, motivo.trim());
    }

    public void actualizarCita(CitaEntity cita) {
        if (cita == null) {
            throw new IllegalArgumentException("La cita no puede estar vacía");
        }

        if (cita.getIdCita() == null || cita.getIdCita() <= 0) {
            throw new IllegalArgumentException("La cita seleccionada no es válida");
        }

        if (cita.getFecha() == null) {
            throw new IllegalArgumentException("La fecha de la cita es obligatoria");
        }

        if (cita.getHoraInicio() == null || cita.getHoraFin() == null) {
            throw new IllegalArgumentException("La hora de inicio y fin son obligatorias");
        }

        if (!cita.getHoraInicio().before(cita.getHoraFin())) {
            throw new IllegalArgumentException("La hora de inicio debe ser menor que la hora de fin");
        }

        CitaEntity citaOriginal = consultarCitaPorId(cita.getIdCita());

        if (!validarAnticipacionMinima12Horas(citaOriginal)) {
            throw new IllegalArgumentException("No se puede reprogramar una cita con menos de 12 horas de anticipación");
        }

        boolean existeTraslape = existeTraslapeParaEdicion(
                cita.getFecha(),
                cita.getHoraInicio(),
                cita.getHoraFin(),
                cita.getIdCita()
        );

        if (existeTraslape) {
            throw new IllegalArgumentException("El horario seleccionado se traslapa con otra cita");
        }

        persistencia.actualizarCita(cita);
    }

    public boolean existeTraslapeParaEdicion(Date fecha, Time horaInicio, Time horaFin, Integer idCita) {
        if (idCita == null || idCita <= 0) {
            throw new IllegalArgumentException("La cita seleccionada no es válida");
        }

        if (fecha == null || horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException("Fecha, hora de inicio y hora de fin son obligatorias");
        }

        if (!horaInicio.before(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser menor que la hora de fin");
        }

        return persistencia.existeTraslapeParaEdicion(fecha, horaInicio, horaFin, idCita);
    }

    private boolean validarAnticipacionMinima12Horas(CitaEntity citaOriginal) {
        if (citaOriginal == null || citaOriginal.getFecha() == null || citaOriginal.getHoraInicio() == null) {
            return false;
        }

        Calendar fechaHoraCitaOriginal = Calendar.getInstance();
        fechaHoraCitaOriginal.setTime(citaOriginal.getFecha());

        Calendar horaOriginal = Calendar.getInstance();
        horaOriginal.setTime(citaOriginal.getHoraInicio());

        fechaHoraCitaOriginal.set(Calendar.HOUR_OF_DAY, horaOriginal.get(Calendar.HOUR_OF_DAY));
        fechaHoraCitaOriginal.set(Calendar.MINUTE, horaOriginal.get(Calendar.MINUTE));
        fechaHoraCitaOriginal.set(Calendar.SECOND, 0);
        fechaHoraCitaOriginal.set(Calendar.MILLISECOND, 0);

        long diferenciaMilisegundos = fechaHoraCitaOriginal.getTimeInMillis() - System.currentTimeMillis();
        long doceHorasMilisegundos = 12L * 60L * 60L * 1000L;

        return diferenciaMilisegundos > doceHorasMilisegundos;
    }
}