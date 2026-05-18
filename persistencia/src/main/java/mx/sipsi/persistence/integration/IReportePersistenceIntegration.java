package mx.sipsi.persistence.integration;

import mx.sipsi.entity.ReporteEntity;

public interface IReportePersistenceIntegration {

    void guardarReporte(ReporteEntity reporte) throws Exception;

    boolean existeReportePorCita(Integer idCita) throws Exception;

    ReporteEntity consultarReportePorCita(Integer idCita) throws Exception;

    ReporteEntity consultarReportePorId(Integer idReporte) throws Exception;

    void actualizarReporte(ReporteEntity reporte) throws Exception;

}