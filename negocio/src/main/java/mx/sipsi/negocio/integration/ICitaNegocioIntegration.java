package mx.sipsi.negocio.integration;

import mx.sipsi.entity.CitaEntity;
import java.sql.Time;
import java.util.Date;
import java.util.List;

public interface ICitaNegocioIntegration {

    void enviarAPersistenciaCita(CitaEntity cita);

    boolean enviarValidacionHorario(Date fecha, Time horaInicio);

    List<CitaEntity> consultarAgenda(int mes, int anio);
}