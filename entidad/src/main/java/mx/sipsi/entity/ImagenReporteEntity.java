package mx.sipsi.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "imagen_reporte")
public class ImagenReporteEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen_reporte")
    private Integer idImagenReporte;

    @Column(name = "nombre_original", nullable = false, length = 255)
    private String nombreOriginal;

    @Column(name = "ruta_servidor", nullable = false, length = 500)
    private String rutaServidor;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_subida", nullable = false)
    private Date fechaSubida;

    @ManyToOne
    @JoinColumn(name = "id_reporte", nullable = false)
    private ReporteEntity reporte;

    public ImagenReporteEntity() {
    }

    @PrePersist
    public void prePersist() {
        if (fechaSubida == null) {
            fechaSubida = new Date();
        }
    }

    public Integer getIdImagenReporte() {
        return idImagenReporte;
    }

    public void setIdImagenReporte(Integer idImagenReporte) {
        this.idImagenReporte = idImagenReporte;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public String getRutaServidor() {
        return rutaServidor;
    }

    public void setRutaServidor(String rutaServidor) {
        this.rutaServidor = rutaServidor;
    }

    public Date getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Date fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public ReporteEntity getReporte() {
        return reporte;
    }

    public void setReporte(ReporteEntity reporte) {
        this.reporte = reporte;
    }
}