package mx.sipsi.persistence.dao;

import mx.sipsi.entity.ReporteEntity;
import mx.sipsi.persistence.integration.IReportePersistenceIntegration;
import mx.sipsi.persistence.persistence.ReportePersistence;

public class ReporteDAO implements IReportePersistenceIntegration {

    private ReportePersistence reportePersistence;

    public ReporteDAO() {
        this.reportePersistence = new ReportePersistence();
    }

    @Override
    public void guardarReporte(ReporteEntity reporte) throws Exception {
        reportePersistence.executePersistReporte(reporte);
    }

    @Override
    public boolean existeReportePorCita(Integer idCita) throws Exception {
        return reportePersistence.executeExisteReportePorCita(idCita);
    }

    @Override
    public ReporteEntity consultarReportePorCita(Integer idCita) throws Exception {
        return reportePersistence.executeSelectReportePorCita(idCita);
    }

    @Override
    public ReporteEntity consultarReportePorId(Integer idReporte) throws Exception {
        return reportePersistence.executeSelectReporteById(idReporte);
    }

    @Override
    public void actualizarReporte(ReporteEntity reporte) throws Exception {
        reportePersistence.executeUpdateReporte(reporte);
    }
}