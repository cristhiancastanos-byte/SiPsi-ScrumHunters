package mx.sipsi.negocio.facade;

import mx.sipsi.entity.CitaEntity;
import mx.sipsi.persistence.dao.CitaDAO;
import mx.sipsi.persistence.integration.ICitaPersistenciaIntegration;

import java.sql.Time;
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
}