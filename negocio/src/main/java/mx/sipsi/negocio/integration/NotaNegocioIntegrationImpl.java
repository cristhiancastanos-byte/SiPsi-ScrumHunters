package mx.sipsi.negocio.integration;

import mx.sipsi.entity.NotaEntity;
import mx.sipsi.persistence.dao.NotaDAO;
import mx.sipsi.persistence.integration.INotaPersistenciaIntegration;

public class NotaNegocioIntegrationImpl implements INotaNegocioIntegration {

    private final INotaPersistenciaIntegration notaPersistenciaIntegration;

    public NotaNegocioIntegrationImpl() {
        this.notaPersistenciaIntegration = new NotaDAO();
    }

    @Override
    public void agregarNota(NotaEntity nota) throws Exception {
        notaPersistenciaIntegration.guardarNota(nota);
    }

    @Override
    public NotaEntity consultarNotaPorId(int idNota) throws Exception {
        return notaPersistenciaIntegration.consultarNotaPorId(idNota);
    }

    @Override
    public void actualizarNota(NotaEntity nota) throws Exception {
        if (nota == null) {
            throw new Exception("La nota clínica no puede estar vacía.");
        }

        if (nota.getContenido() == null || nota.getContenido().trim().isEmpty()) {
            throw new Exception("El contenido de la nota clínica no puede estar vacío.");
        }

        nota.setContenido(nota.getContenido().trim());

        if (nota.getTitulo() != null) {
            nota.setTitulo(nota.getTitulo().trim());
        }

        notaPersistenciaIntegration.actualizarNota(nota);
    }

    @Override
    public void eliminarNota(int idNota) throws Exception {
        if (idNota <= 0) {
            throw new Exception("No se encontró la nota clínica seleccionada.");
        }

        NotaEntity notaEncontrada = notaPersistenciaIntegration.consultarNotaPorId(idNota);

        if (notaEncontrada == null) {
            throw new Exception("La nota clínica seleccionada no existe.");
        }

        notaPersistenciaIntegration.eliminarNota(idNota);
    }
}