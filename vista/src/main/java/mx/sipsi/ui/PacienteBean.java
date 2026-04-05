package mx.sipsi.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.facade.PacienteFacade;

@Named("pacienteBean")
@ViewScoped
public class PacienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final PacienteFacade facade = new PacienteFacade();

    // LISTADO
    private List<PacienteEntity> listaPacientes = new ArrayList<>();
    private String terminoBusqueda;

    // REGISTRO
    private PacienteEntity pacienteNuevo = new PacienteEntity();
    private boolean duplicadoError;
    private String dia = "";
    private String mes = "";
    private String anio = "";

    // EDICIÓN
    private PacienteEntity pacienteEditar = new PacienteEntity();
    private String diaEditar = "";
    private String mesEditar = "";
    private String anioEditar = "";
    private boolean errorNombreEditar;
    private boolean errorFechaEditar;
    private boolean errorGeneroEditar;
    private boolean duplicadoEditar;

    // APOYO
    private List<String> listaDias = new ArrayList<>();
    private List<String> listaAnios = new ArrayList<>();

    @PostConstruct
    public void init() {
        cargarAnios();
        actualizarDias();
        cargarPacientes();
    }

    // =========================
    // LISTADO
    // =========================
    public void cargarPacientes() {
        if (terminoBusqueda == null || terminoBusqueda.trim().isEmpty()) {
            listaPacientes = facade.buscarTodosActivos();
        } else {
            listaPacientes = facade.buscarPorNombreActivos(terminoBusqueda.trim());
        }
    }

    public void filtrarPorNombre() {
        cargarPacientes();
    }

    // =========================
    // REGISTRO
    // =========================
    public void guardar() {
        duplicadoError = false;

        try {
            if (pacienteNuevo.getNombre() == null || pacienteNuevo.getNombre().trim().isEmpty()
                    || dia == null || dia.isEmpty()
                    || mes == null || mes.isEmpty()
                    || anio == null || anio.isEmpty()
                    || pacienteNuevo.getGenero() == null || pacienteNuevo.getGenero().trim().isEmpty()) {
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }

            Date fecha = construirFecha(dia, mes, anio);
            pacienteNuevo.setFechaNac(fecha);

            facade.procesarAlta(pacienteNuevo);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Paciente registrado correctamente",
                            "Paciente registrado correctamente"));

            limpiar();
            cargarPacientes();

        } catch (IllegalArgumentException e) {
            if ("Paciente ya existe".equalsIgnoreCase(e.getMessage())) {
                duplicadoError = true;
                FacesContext.getCurrentInstance().validationFailed();
                return;
            } else if ("Complete todos los campos obligatorios".equalsIgnoreCase(e.getMessage())) {
                FacesContext.getCurrentInstance().validationFailed();
                return;
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "No se pudo registrar el paciente"));
        }
    }

    public void limpiar() {
        pacienteNuevo = new PacienteEntity();
        duplicadoError = false;
        dia = "";
        mes = "";
        anio = "";
        actualizarDias();
    }

    // =========================
    // EDICIÓN
    // =========================
    public void abrirEdicion(int id) {
        limpiarErroresEdicion();

        pacienteEditar = facade.procesarConsultaPorId(id);

        if (pacienteEditar != null && pacienteEditar.getFechaNac() != null) {
            SimpleDateFormat sdfDia = new SimpleDateFormat("dd");
            SimpleDateFormat sdfMes = new SimpleDateFormat("MM");
            SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");

            diaEditar = sdfDia.format(pacienteEditar.getFechaNac());
            mesEditar = sdfMes.format(pacienteEditar.getFechaNac());
            anioEditar = sdfAnio.format(pacienteEditar.getFechaNac());
        }
    }

    public void actualizarPaciente() {
        limpiarErroresEdicion();

        if (pacienteEditar.getNombre() == null || pacienteEditar.getNombre().trim().isEmpty()) {
            errorNombreEditar = true;
        }

        if (diaEditar == null || diaEditar.isEmpty()
                || mesEditar == null || mesEditar.isEmpty()
                || anioEditar == null || anioEditar.isEmpty()) {
            errorFechaEditar = true;
        }

        if (pacienteEditar.getGenero() == null || pacienteEditar.getGenero().trim().isEmpty()) {
            errorGeneroEditar = true;
        }

        if (errorNombreEditar || errorFechaEditar || errorGeneroEditar) {
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        try {
            Date fecha = construirFecha(diaEditar, mesEditar, anioEditar);
            pacienteEditar.setFechaNac(fecha);

            facade.procesarActualizacion(pacienteEditar);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Paciente actualizado correctamente",
                            "Paciente actualizado correctamente"));

            cargarPacientes();

        } catch (IllegalArgumentException e) {
            if ("Paciente ya existe".equalsIgnoreCase(e.getMessage())) {
                duplicadoEditar = true;
                FacesContext.getCurrentInstance().validationFailed();
                return;
            } else if ("Complete todos los campos obligatorios".equalsIgnoreCase(e.getMessage())) {
                errorNombreEditar = pacienteEditar.getNombre() == null || pacienteEditar.getNombre().trim().isEmpty();
                errorFechaEditar = diaEditar == null || diaEditar.isEmpty()
                        || mesEditar == null || mesEditar.isEmpty()
                        || anioEditar == null || anioEditar.isEmpty();
                errorGeneroEditar = pacienteEditar.getGenero() == null || pacienteEditar.getGenero().trim().isEmpty();
                FacesContext.getCurrentInstance().validationFailed();
                return;
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "No se pudo actualizar el paciente"));
        }
    }

    public void cancelarEdicion() {
        limpiarErroresEdicion();
    }

    public void limpiarErroresEdicion() {
        errorNombreEditar = false;
        errorFechaEditar = false;
        errorGeneroEditar = false;
        duplicadoEditar = false;
    }

    // =========================
    // APOYO
    // =========================
    public void actualizarDias() {
        listaDias.clear();

        if (mes == null || mes.isEmpty() || anio == null || anio.isEmpty()) {
            for (int i = 1; i <= 31; i++) {
                listaDias.add(String.format("%02d", i));
            }
            return;
        }

        int mesInt = Integer.parseInt(mes);
        int anioInt = Integer.parseInt(anio);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, anioInt);
        calendar.set(Calendar.MONTH, mesInt - 1);

        int maxDias = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= maxDias; i++) {
            listaDias.add(String.format("%02d", i));
        }

        if (dia != null && !dia.isEmpty()) {
            int diaInt = Integer.parseInt(dia);
            if (diaInt > maxDias) {
                dia = "";
            }
        }
    }

    private void cargarAnios() {
        listaAnios.clear();
        int actual = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = actual; i >= 1950; i--) {
            listaAnios.add(String.valueOf(i));
        }
    }

    private Date construirFecha(String d, String m, String a) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        return sdf.parse(d + "/" + m + "/" + a);
    }

    // GETTERS Y SETTERS
    public List<PacienteEntity> getListaPacientes() {
        return listaPacientes;
    }

    public void setListaPacientes(List<PacienteEntity> listaPacientes) {
        this.listaPacientes = listaPacientes;
    }

    public String getTerminoBusqueda() {
        return terminoBusqueda;
    }

    public void setTerminoBusqueda(String terminoBusqueda) {
        this.terminoBusqueda = terminoBusqueda;
    }

    public PacienteEntity getPacienteNuevo() {
        return pacienteNuevo;
    }

    public void setPacienteNuevo(PacienteEntity pacienteNuevo) {
        this.pacienteNuevo = pacienteNuevo;
    }

    public boolean isDuplicadoError() {
        return duplicadoError;
    }

    public void setDuplicadoError(boolean duplicadoError) {
        this.duplicadoError = duplicadoError;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public PacienteEntity getPacienteEditar() {
        return pacienteEditar;
    }

    public void setPacienteEditar(PacienteEntity pacienteEditar) {
        this.pacienteEditar = pacienteEditar;
    }

    public String getDiaEditar() {
        return diaEditar;
    }

    public void setDiaEditar(String diaEditar) {
        this.diaEditar = diaEditar;
    }

    public String getMesEditar() {
        return mesEditar;
    }

    public void setMesEditar(String mesEditar) {
        this.mesEditar = mesEditar;
    }

    public String getAnioEditar() {
        return anioEditar;
    }

    public void setAnioEditar(String anioEditar) {
        this.anioEditar = anioEditar;
    }

    public boolean isErrorNombreEditar() {
        return errorNombreEditar;
    }

    public void setErrorNombreEditar(boolean errorNombreEditar) {
        this.errorNombreEditar = errorNombreEditar;
    }

    public boolean isErrorFechaEditar() {
        return errorFechaEditar;
    }

    public void setErrorFechaEditar(boolean errorFechaEditar) {
        this.errorFechaEditar = errorFechaEditar;
    }

    public boolean isErrorGeneroEditar() {
        return errorGeneroEditar;
    }

    public void setErrorGeneroEditar(boolean errorGeneroEditar) {
        this.errorGeneroEditar = errorGeneroEditar;
    }

    public boolean isDuplicadoEditar() {
        return duplicadoEditar;
    }

    public void setDuplicadoEditar(boolean duplicadoEditar) {
        this.duplicadoEditar = duplicadoEditar;
    }

    public List<String> getListaDias() {
        return listaDias;
    }

    public void setListaDias(List<String> listaDias) {
        this.listaDias = listaDias;
    }

    public List<String> getListaAnios() {
        return listaAnios;
    }

    public void setListaAnios(List<String> listaAnios) {
        this.listaAnios = listaAnios;
    }
}