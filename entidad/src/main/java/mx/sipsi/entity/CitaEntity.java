package mx.sipsi.entity;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "cita")
public class CitaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Integer idCita;

    @Column(name = "id_paciente", nullable = false)
    private Integer idPaciente;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @Column(name = "hora_inicio", nullable = false)
    private Time horaInicio;

    @Column(name = "hora_fin")
    private Time horaFin;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "estado", length = 50)
    private String estado;

    public CitaEntity() {
        this.estado = "Agendada";
    }

    public Integer getIdCita() { return idCita; }
    public void setIdCita(Integer idCita) { this.idCita = idCita; }

    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer idPaciente) { this.idPaciente = idPaciente; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Time getHoraInicio() { return horaInicio; }
    public void setHoraInicio(Time horaInicio) { this.horaInicio = horaInicio; }

    public Time getHoraFin() { return horaFin; }
    public void setHoraFin(Time horaFin) { this.horaFin = horaFin; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}