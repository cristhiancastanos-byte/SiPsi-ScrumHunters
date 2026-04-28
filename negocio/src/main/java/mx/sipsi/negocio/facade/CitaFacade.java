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
        persistencia.save(cita);
    }

    public boolean procesarBusquedaEmpalme(Date fecha, Time horaInicio) {
        CitaEntity citaExistente = persistencia.findByFechaHora(fecha, horaInicio);
        return citaExistente != null;
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