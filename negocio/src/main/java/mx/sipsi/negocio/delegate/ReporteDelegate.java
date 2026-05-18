package mx.sipsi.negocio.delegate;

import mx.sipsi.entity.ReporteEntity;
import mx.sipsi.negocio.facade.ReporteFacade;

public class ReporteDelegate {

    private ReporteFacade reporteFacade;

    public ReporteDelegate() {
        this.reporteFacade = new ReporteFacade();
    }

    public void crearReporte(ReporteEntity reporte) throws Exception {
        reporteFacade.procesarCreacionReporte(reporte);
    }

    public boolean existeReportePorCita(Integer idCita) throws Exception {
        return reporteFacade.existeReportePorCita(idCita);
    }

    public ReporteEntity consultarReportePorId(Integer idReporte) throws Exception {
        return reporteFacade.procesarConsultaReportePorId(idReporte);
    }

    public ReporteEntity consultarReportePorCita(Integer idCita) throws Exception {
        return reporteFacade.procesarConsultaReportePorCita(idCita);
    }

    public void actualizarReporte(ReporteEntity reporte) throws Exception {
        reporteFacade.procesarActualizacionReporte(reporte);
    }
}