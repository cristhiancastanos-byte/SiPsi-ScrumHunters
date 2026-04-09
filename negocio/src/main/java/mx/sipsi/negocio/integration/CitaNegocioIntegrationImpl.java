package mx.sipsi.negocio.integration;

import mx.sipsi.entity.CitaEntity;
import mx.sipsi.negocio.facade.CitaFacade;
import java.sql.Time;
import java.util.Date;

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
    public boolean enviarValidacionHorario(Date fecha, Time hora) {
        return facade.procesarBusquedaEmpalme(fecha, hora);
    }
}