package mx.sipsi.helper;

import mx.sipsi.entity.CitaEntity;
import mx.sipsi.negocio.delegate.CitaDelegate;
import java.sql.Time;
import java.util.Date;

public class CitaHelper {

    private CitaDelegate citaDelegate;

    public CitaHelper() {
        this.citaDelegate = new CitaDelegate();
    }

    public boolean validarDatos(CitaEntity cita) {
        if (cita == null) return false;
        return cita.getIdPaciente() != null && cita.getIdPaciente() > 0
                && cita.getFecha() != null
                && cita.getHoraInicio() != null;
    }

    public boolean validarEmpalmeHorario(Date fecha, Time horaInicio) {
        return citaDelegate.validarDisponibilidad(fecha, horaInicio);
    }

    public CitaDelegate getCitaDelegate() {
        return citaDelegate;
    }
}