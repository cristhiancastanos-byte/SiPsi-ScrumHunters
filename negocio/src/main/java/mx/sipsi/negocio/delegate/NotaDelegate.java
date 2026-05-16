package mx.sipsi.negocio.delegate;

import mx.sipsi.entity.NotaEntity;
import mx.sipsi.negocio.facade.NotaFacade;

public class NotaDelegate {

    private final NotaFacade notaFacade;

    public NotaDelegate() {
        this.notaFacade = new NotaFacade();
    }

    public void agregarNota(NotaEntity nota) throws Exception {
        notaFacade.agregarNota(nota);
    }

    public NotaEntity consultarNotaPorId(int idNota) throws Exception {
        return notaFacade.consultarNotaPorId(idNota);
    }

    public void actualizarNota(NotaEntity nota) throws Exception {
        notaFacade.actualizarNota(nota);
    }

    public void eliminarNota(int idNota) throws Exception {
        notaFacade.eliminarNota(idNota);
    }
}