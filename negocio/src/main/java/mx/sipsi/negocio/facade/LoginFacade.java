package mx.sipsi.negocio.facade;

import mx.sipsi.entity.UsuarioEntity;
import mx.sipsi.persistence.dao.UsuarioDAO;

public class LoginFacade {

    private UsuarioDAO usuarioDAO;

    public LoginFacade() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public UsuarioEntity validarAcceso(String correo, String contrasena) {


        if (correo == null || correo.trim().isEmpty() ||
                contrasena == null || contrasena.trim().isEmpty()) {

            System.out.println("Validacion fallida (campos nulos o vacios)");
            return null;
        }

        try {
            return usuarioDAO.login(correo, contrasena);
        } catch (Exception e) {
            System.err.println("Error en LoginFacade: " + e.getMessage());
            return null;
        }
    }
}