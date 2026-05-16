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

    public boolean validarDisponibilidad(Date fecha, Time horaInicio, Time horaFin) {
        return integracion.enviarValidacionHorario(fecha, horaInicio, horaFin);
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

    public CitaEntity consultarCitaPorId(Integer idCita) {
        return integracion.consultarCitaPorId(idCita);
    }

    public void cancelarCita(Integer idCita, String motivo) {
        integracion.cancelarCita(idCita, motivo);
    }

    public void actualizarCita(CitaEntity cita) {
        integracion.actualizarCita(cita);
    }
}