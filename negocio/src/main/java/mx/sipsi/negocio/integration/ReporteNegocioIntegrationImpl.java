package mx.sipsi.negocio.integration;

import mx.sipsi.entity.ReporteEntity;
import mx.sipsi.persistence.dao.ReporteDAO;
import mx.sipsi.persistence.integration.IReportePersistenceIntegration;

public class ReporteNegocioIntegrationImpl implements IReporteNegocioIntegration {

    private IReportePersistenceIntegration reporteDAO;

    public ReporteNegocioIntegrationImpl() {
        this.reporteDAO = new ReporteDAO();
    }

    @Override
    public void crearReporte(ReporteEntity reporte) throws Exception {

        if (reporte == null) {
            throw new Exception("Ingrese texto para la nota");
        }

        if (reporte.getContenido() == null || reporte.getContenido().trim().isEmpty()) {
            throw new Exception("Ingrese texto para la nota");
        }

        if (reporte.getIdCita() == null) {
            throw new Exception("No se seleccionó una cita válida");
        }

        if (existeReportePorCita(reporte.getIdCita())) {
            throw new Exception("Ya existe un reporte para esta cita");
        }

        reporte.setContenido(reporte.getContenido().trim());

        reporteDAO.guardarReporte(reporte);
    }

    @Override
    public boolean existeReportePorCita(Integer idCita) throws Exception {

        if (idCita == null) {
            return false;
        }

        return reporteDAO.existeReportePorCita(idCita);
    }
}