package mx.sipsi.ui;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import mx.sipsi.negocio.facade.LoginFacade;
import mx.sipsi.entity.UsuarioEntity;

@Named
@SessionScoped
public class LoginBean implements Serializable {
    private String correo;
    private String contrasena;
    private boolean errorLogin = false;
    private LoginFacade loginFacade = new LoginFacade();

    public String entrar() {

        this.errorLogin = false;

        UsuarioEntity usuario = loginFacade.validarAcceso(correo, contrasena);

        if (usuario != null) {


            return "agenda.xhtml?faces-redirect=true";
        } else {
            this.errorLogin = true;
            FacesContext.getCurrentInstance().addMessage("loginForm:pass",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario o contraseña incorrectos", ""));

            return null;
        }
    }

    public boolean isErrorLogin() { return errorLogin; }
    public void setErrorLogin(boolean errorLogin) { this.errorLogin = errorLogin; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}