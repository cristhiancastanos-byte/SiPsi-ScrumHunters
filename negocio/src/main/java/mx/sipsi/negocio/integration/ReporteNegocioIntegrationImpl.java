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
            throw new Exception("Ingrese texto para el reporte");
        }

        if (reporte.getContenido() == null || reporte.getContenido().trim().isEmpty()) {
            throw new Exception("Ingrese texto para el reporte");
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

    @Override
    public ReporteEntity consultarReportePorCita(Integer idCita) throws Exception {

        if (idCita == null) {
            throw new Exception("No se seleccionó una cita válida");
        }

        ReporteEntity reporte = reporteDAO.consultarReportePorCita(idCita);

        if (reporte == null) {
            throw new Exception("No se encontró el reporte clínico de la cita");
        }

        return reporte;
    }

    @Override
    public ReporteEntity consultarReportePorId(Integer idReporte) throws Exception {

        if (idReporte == null) {
            throw new Exception("No se seleccionó un reporte válido");
        }

        ReporteEntity reporte = reporteDAO.consultarReportePorId(idReporte);

        if (reporte == null) {
            throw new Exception("No se encontró el reporte clínico seleccionado");
        }

        return reporte;
    }

    @Override
    public void actualizarReporte(ReporteEntity reporte) throws Exception {

        if (reporte == null) {
            throw new Exception("No se seleccionó un reporte válido");
        }

        if (reporte.getIdReporte() == null) {
            throw new Exception("No se seleccionó un reporte válido");
        }

        if (reporte.getContenido() == null || reporte.getContenido().trim().isEmpty()) {
            throw new Exception("Ingrese texto para el reporte");
        }

        ReporteEntity reporteExistente = consultarReportePorId(reporte.getIdReporte());

        reporteExistente.setContenido(reporte.getContenido().trim());

        reporteDAO.actualizarReporte(reporteExistente);
    }
}