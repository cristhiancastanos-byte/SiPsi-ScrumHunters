package mx.sipsi.negocio.facade;

import mx.sipsi.entity.CitaEntity;
import mx.sipsi.persistence.dao.CitaDAO;
import mx.sipsi.persistence.integration.ICitaPersistenciaIntegration;
import java.sql.Time;
import java.util.Date;

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
}