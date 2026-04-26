package mx.sipsi.negocio.integration;

import mx.sipsi.entity.CitaEntity;
import mx.sipsi.negocio.facade.CitaFacade;
import java.sql.Time;
import java.util.Date;
import java.util.List;

public class CitaNegocioIntegrationImpl implements ICitaNegocioIntegration {

    private CitaFacade facade;

    public CitaNegocioIntegrationImpl() {
        this.facade = new CitaFacade();
    }

    @Override
    public void enviarAPersistenciaCita(CitaEntity cita) {
        facade.procesarRegistro(cita);
    }

    @Override
    public boolean enviarValidacionHorario(Date fecha, Time horaInicio) {
        return facade.procesarBusquedaEmpalme(fecha, horaInicio);
    }

    @Override
    public List<CitaEntity> consultarAgenda(int mes, int anio) {
        return facade.consultarAgenda(mes, anio);
    }

    @Override
    public boolean tieneCitasPendientesPorPaciente(int idPaciente) {
        return facade.procesarValidacionCitasPendientes(idPaciente);
    }

    @Override
    public void eliminarCitasPendientesPorPaciente(int idPaciente) {
        facade.procesarEliminacionCitasPendientes(idPaciente);
    }
}