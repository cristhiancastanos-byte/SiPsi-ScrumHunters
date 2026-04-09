package mx.sipsi.persistence.integration;

import mx.sipsi.entity.CitaEntity;
import java.sql.Time;
import java.util.Date;

public interface ICitaPersistenciaIntegration {

    void save(CitaEntity cita);

    CitaEntity findByFechaHora(Date fecha, Time horaInicio);
}