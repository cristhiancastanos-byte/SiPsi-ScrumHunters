package mx.sipsi.negocio.integration;

import mx.sipsi.entity.ReporteEntity;

public interface IReporteNegocioIntegration {

    void crearReporte(ReporteEntity reporte) throws Exception;

    boolean existeReportePorCita(Integer idCita) throws Exception;
}