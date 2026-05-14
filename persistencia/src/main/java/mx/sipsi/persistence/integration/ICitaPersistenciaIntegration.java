package mx.sipsi.persistence.integration;

import mx.sipsi.entity.CitaEntity;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public interface ICitaPersistenciaIntegration {

    void save(CitaEntity cita);

    CitaEntity findByFechaHora(Date fecha, Time horaInicio);

    boolean existeTraslape(Date fecha, Time horaInicio, Time horaFin);

    List<CitaEntity> obtenerCitasPorMes(int mes, int anio);

    boolean tieneCitasPendientesPorPaciente(int idPaciente);

    void eliminarCitasPendientesPorPaciente(int idPaciente);

    CitaEntity consultarCitaPorId(Integer idCita);

    void actualizarCita(CitaEntity cita);

    boolean existeTraslapeParaEdicion(Date fecha, Time horaInicio, Time horaFin, Integer idCita);
}