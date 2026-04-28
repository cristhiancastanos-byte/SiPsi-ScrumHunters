package mx.sipsi.negocio.delegate;

import mx.sipsi.entity.CitaEntity;
import mx.sipsi.negocio.integration.ICitaNegocioIntegration;
import mx.sipsi.negocio.integration.CitaNegocioIntegrationImpl;
import java.sql.Time;
import java.util.Date;
import java.util.List;

public class CitaDelegate {

    private ICitaNegocioIntegration integracion;

    public CitaDelegate() {
        this.integracion = new CitaNegocioIntegrationImpl();
    }

    public void registrarCita(CitaEntity cita) {
        integracion.enviarAPersistenciaCita(cita);
    }

    public boolean validarDisponibilidad(Date fecha, Time horaInicio) {
        return integracion.enviarValidacionHorario(fecha, horaInicio);
    }

    public List<CitaEntity> consultarAgenda(int mes, int anio) {
        return integracion.consultarAgenda(mes, anio);
    }

    public boolean tieneCitasPendientesPorPaciente(int idPaciente) {
        return integracion.tieneCitasPendientesPorPaciente(idPaciente);
    }

    public void eliminarCitasPendientesPorPaciente(int idPaciente) {
        integracion.eliminarCitasPendientesPorPaciente(idPaciente);
    }
}