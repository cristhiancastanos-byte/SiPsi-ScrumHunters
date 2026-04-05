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

    private PacienteEntity pacienteNuevo;
    private String dia = "";
    private String mes = "";
    private String anio = "";
    private List<String> listaDias;
    private List<String> listaAnios;
    private boolean duplicadoError = false;

    private List<PacienteEntity> listaPacientes;
    private String terminoBusqueda;

    private PacienteEntity pacienteEditar = new PacienteEntity();
    private String diaEditar = "";
    private String mesEditar = "";
    private String anioEditar = "";
    private List<String> listaDiasEditar;
    private boolean errorNombreEditar;
    private boolean errorFechaEditar;
    private boolean errorGeneroEditar;
    private boolean duplicadoEditar;

    @PostConstruct
    public void init() {
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        listaAnios = new ArrayList<>();
        for (int i = anioActual; i >= 1926; i--) {
            listaAnios.add(String.valueOf(i));
        }
        limpiar();
        cargarTodosActivos();
    }

    public void cargarTodosActivos() {
        try {
            listaPacientes = facade.buscarTodosActivos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filtrarPorNombre() {
        try {
            if (terminoBusqueda != null && !terminoBusqueda.trim().isEmpty()) {
                listaPacientes = facade.buscarPorNombreActivos(terminoBusqueda.trim());
            } else {
                cargarTodosActivos();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizarDias() {
        int maxDias = 31;
        if (mes != null && !mes.isEmpty()) {
            int mesInt = Integer.parseInt(mes);
            int anioInt = (anio != null && !anio.isEmpty()) ? Integer.parseInt(anio) : Calendar.getInstance().get(Calendar.YEAR);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, anioInt);
            cal.set(Calendar.MONTH, mesInt - 1);
            maxDias = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        listaDias = new ArrayList<>();
        for (int i = 1; i <= maxDias; i++) {
            listaDias.add(String.format("%02d", i));
        }

        if (dia != null && !dia.isEmpty() && Integer.parseInt(dia) > maxDias) {
            this.dia = String.format("%02d", maxDias);
        }
    }

    public void actualizarDiasEditar() {
        int maxDias = 31;
        if (mesEditar != null && !mesEditar.isEmpty()) {
            int mesInt = Integer.parseInt(mesEditar);
            int anioInt = (anioEditar != null && !anioEditar.isEmpty()) ? Integer.parseInt(anioEditar) : Calendar.getInstance().get(Calendar.YEAR);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, anioInt);
            cal.set(Calendar.MONTH, mesInt - 1);
            maxDias = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        listaDiasEditar = new ArrayList<>();
        for (int i = 1; i <= maxDias; i++) {
            listaDiasEditar.add(String.format("%02d", i));
        }

        if (diaEditar != null && !diaEditar.isEmpty() && Integer.parseInt(diaEditar) > maxDias) {
            this.diaEditar = String.format("%02d", maxDias);
        }
    }

    public void guardar() {
        duplicadoError = false;

        if (pacienteNuevo.getNombre() == null || pacienteNuevo.getNombre().trim().isEmpty() ||
                dia == null || dia.isEmpty() || mes == null || mes.isEmpty() || anio == null || anio.isEmpty() ||
                pacienteNuevo.getGenero() == null || pacienteNuevo.getGenero().isEmpty()) {
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        try {
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(anio), Integer.parseInt(mes) - 1, Integer.parseInt(dia));
            Date fechaNac = cal.getTime();
            pacienteNuevo.setFechaNac(fechaNac);

            if (pacienteNuevo.getCorreo() != null && pacienteNuevo.getCorreo().trim().isEmpty()) {
                pacienteNuevo.setCorreo(null);
            }

            facade.procesarAlta(pacienteNuevo);

            cargarTodosActivos();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Paciente registrado correctamente"));

            limpiar();
            org.primefaces.PrimeFaces.current().executeScript("PF('dlgRegistro').hide()");

        } catch (IllegalArgumentException e) {
            if ("Paciente ya existe".equalsIgnoreCase(e.getMessage())) {
                duplicadoError = true;
                FacesContext.getCurrentInstance().validationFailed();
            } else if ("Complete todos los campos obligatorios".equalsIgnoreCase(e.getMessage())) {
                FacesContext.getCurrentInstance().validationFailed();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el paciente: " + e.getMessage()));
        }
    }

    public void limpiar() {
        pacienteNuevo = new PacienteEntity();
        dia = ""; mes = ""; anio = "";
        duplicadoError = false;
        actualizarDias();
    }

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

            actualizarDiasEditar();
        }
    }

    public void actualizarPaciente() {
        limpiarErroresEdicion();
        boolean hasErrors = false;

        if (pacienteEditar.getNombre() == null || pacienteEditar.getNombre().trim().isEmpty()) {
            errorNombreEditar = true;
            hasErrors = true;
        }

        if (diaEditar == null || diaEditar.isEmpty() || mesEditar == null || mesEditar.isEmpty() || anioEditar == null || anioEditar.isEmpty()) {
            errorFechaEditar = true;
            hasErrors = true;
        }

        if (pacienteEditar.getGenero() == null || pacienteEditar.getGenero().trim().isEmpty()) {
            errorGeneroEditar = true;
            hasErrors = true;
        }

        if (hasErrors) {
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        try {
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(anioEditar), Integer.parseInt(mesEditar) - 1, Integer.parseInt(diaEditar));
            Date fechaNac = cal.getTime();
            pacienteEditar.setFechaNac(fechaNac);

            if (pacienteEditar.getCorreo() != null && pacienteEditar.getCorreo().trim().isEmpty()) {
                pacienteEditar.setCorreo(null);
            }

            facade.procesarActualizacion(pacienteEditar);

            cargarTodosActivos();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Paciente actualizado correctamente"));

            org.primefaces.PrimeFaces.current().executeScript("PF('dlgEditar').hide()");

        } catch (IllegalArgumentException e) {
            if ("Paciente ya existe".equalsIgnoreCase(e.getMessage())) {
                duplicadoEditar = true;
                FacesContext.getCurrentInstance().validationFailed();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar el paciente"));
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

    public List<PacienteEntity> getListaPacientes() { return listaPacientes; }
    public void setListaPacientes(List<PacienteEntity> listaPacientes) { this.listaPacientes = listaPacientes; }

    public String getTerminoBusqueda() { return terminoBusqueda; }
    public void setTerminoBusqueda(String terminoBusqueda) { this.terminoBusqueda = terminoBusqueda; }

    public PacienteEntity getPacienteNuevo() { return pacienteNuevo; }
    public void setPacienteNuevo(PacienteEntity pacienteNuevo) { this.pacienteNuevo = pacienteNuevo; }

    public boolean isDuplicadoError() { return duplicadoError; }
    public void setDuplicadoError(boolean duplicadoError) { this.duplicadoError = duplicadoError; }

    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }

    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }

    public String getAnio() { return anio; }
    public void setAnio(String anio) { this.anio = anio; }

    public PacienteEntity getPacienteEditar() { return pacienteEditar; }
    public void setPacienteEditar(PacienteEntity pacienteEditar) { this.pacienteEditar = pacienteEditar; }

    public String getDiaEditar() { return diaEditar; }
    public void setDiaEditar(String diaEditar) { this.diaEditar = diaEditar; }

    public String getMesEditar() { return mesEditar; }
    public void setMesEditar(String mesEditar) { this.mesEditar = mesEditar; }

    public String getAnioEditar() { return anioEditar; }
    public void setAnioEditar(String anioEditar) { this.anioEditar = anioEditar; }

    public boolean isErrorNombreEditar() { return errorNombreEditar; }
    public void setErrorNombreEditar(boolean errorNombreEditar) { this.errorNombreEditar = errorNombreEditar; }

    public boolean isErrorFechaEditar() { return errorFechaEditar; }
    public void setErrorFechaEditar(boolean errorFechaEditar) { this.errorFechaEditar = errorFechaEditar; }

    public boolean isErrorGeneroEditar() { return errorGeneroEditar; }
    public void setErrorGeneroEditar(boolean errorGeneroEditar) { this.errorGeneroEditar = errorGeneroEditar; }

    public boolean isDuplicadoEditar() { return duplicadoEditar; }
    public void setDuplicadoEditar(boolean duplicadoEditar) { this.duplicadoEditar = duplicadoEditar; }

    public List<String> getListaDias() { return listaDias; }
    public void setListaDias(List<String> listaDias) { this.listaDias = listaDias; }

    public List<String> getListaDiasEditar() { return listaDiasEditar; }
    public void setListaDiasEditar(List<String> listaDiasEditar) { this.listaDiasEditar = listaDiasEditar; }

    public List<String> getListaAnios() { return listaAnios; }
    public void setListaAnios(List<String> listaAnios) { this.listaAnios = listaAnios; }
}