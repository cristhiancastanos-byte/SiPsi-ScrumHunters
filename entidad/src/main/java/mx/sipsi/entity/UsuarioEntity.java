package mx.sipsi.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "usuario")
public class UsuarioEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id_usuario;

    @Column(name = "correo_electronico", nullable = false, unique = true)
    private String correo_electronico;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "rol_psicologa")
    private String rol_psicologa;


    public UsuarioEntity() {
    }

    public Long getId_usuario() { return id_usuario; }
    public void setId_usuario(Long id_usuario) { this.id_usuario = id_usuario; }

    public String getCorreo_electronico() { return correo_electronico; }
    public void setCorreo_electronico(String correo_electronico) { this.correo_electronico = correo_electronico; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol_psicologa() { return rol_psicologa; }
    public void setRol_psicologa(String rol_psicologa) { this.rol_psicologa = rol_psicologa; }
}