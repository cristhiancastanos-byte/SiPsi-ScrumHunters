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
}