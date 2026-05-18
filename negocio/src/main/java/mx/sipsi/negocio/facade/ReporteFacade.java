package mx.sipsi.negocio.facade;

import mx.sipsi.entity.ReporteEntity;
import mx.sipsi.negocio.integration.IReporteNegocioIntegration;
import mx.sipsi.negocio.integration.ReporteNegocioIntegrationImpl;

public class ReporteFacade {

    private IReporteNegocioIntegration reporteNegocioIntegration;

    public ReporteFacade() {
        this.reporteNegocioIntegration = new ReporteNegocioIntegrationImpl();
    }

    public void procesarCreacionReporte(ReporteEntity reporte) throws Exception {
        reporteNegocioIntegration.crearReporte(reporte);
    }

    public boolean existeReportePorCita(Integer idCita) throws Exception {
        return reporteNegocioIntegration.existeReportePorCita(idCita);
    }

    public ReporteEntity procesarConsultaReportePorId(Integer idReporte) throws Exception {
        return reporteNegocioIntegration.consultarReportePorId(idReporte);
    }

    public ReporteEntity procesarConsultaReportePorCita(Integer idCita) throws Exception {
        return reporteNegocioIntegration.consultarReportePorCita(idCita);
    }

    public void procesarActualizacionReporte(ReporteEntity reporte) throws Exception {
        reporteNegocioIntegration.actualizarReporte(reporte);
    }
}