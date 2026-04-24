package mx.sipsi.persistence.dao;

import mx.sipsi.entity.CitaEntity;
import mx.sipsi.persistence.integration.ICitaPersistenciaIntegration;
import mx.sipsi.persistence.persistence.CitaPersistence;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class CitaDAO implements ICitaPersistenciaIntegration {

    private CitaPersistence persistence;

    public CitaDAO() {
        this.persistence = new CitaPersistence();
    }

    public void insertar(CitaEntity cita) {
        persistence.executeTransaction(cita);
    }

    public CitaEntity buscarPorFechaYHora(Date fecha, Time horaInicio) {
        return persistence.executeFindEmpalme(fecha, horaInicio);
    }

    @Override
    public void save(CitaEntity cita) {
        this.insertar(cita);
    }

    @Override
    public CitaEntity findByFechaHora(Date fecha, Time horaInicio) {
        return this.buscarPorFechaYHora(fecha, horaInicio);
    }

    @Override
    public List<CitaEntity> obtenerCitasPorMes(int mes, int anio) {
        return persistence.executeSelectCitasPorMes(mes, anio);
    }
}