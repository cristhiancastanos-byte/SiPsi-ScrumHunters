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
    private boolean correoDuplicadoError = false;
    private boolean telefonoDuplicadoError = false;

    private List<PacienteEntity> listaPacientes;
    private String terminoBusqueda;

    private PacienteEntity pacienteEditar = new PacienteEntity();
    private PacienteEntity pacienteEliminar = new PacienteEntity();
    private String diaEditar = "";
    private String mesEditar = "";
    private String anioEditar = "";
    private List<String> listaDiasEditar;


    private boolean errorNombreEditar;
    private boolean errorFechaEditar;
    private boolean errorGeneroEditar;
    private boolean errorTelefonoEditar;
    private boolean duplicadoEditar;
    private boolean correoDuplicadoEditar;
    private boolean telefonoDuplicadoEditar;

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
            int anioInt = (anio != null && !anio.isEmpty())
                    ? Integer.parseInt(anio)
                    : Calendar.getInstance().get(Calendar.YEAR);

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
            int anioInt = (anioEditar != null && !anioEditar.isEmpty())
                    ? Integer.parseInt(anioEditar)
                    : Calendar.getInstance().get(Calendar.YEAR);

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

    private boolean telefonoYaExiste(String telefono, int idActual) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }

        cargarTodosActivos();

        String telefonoNormalizado = telefono.trim();

        if (listaPacientes == null) {
            return false;
        }

        for (PacienteEntity paciente : listaPacientes) {
            if (paciente != null
                    && paciente.getTelefono() != null
                    && paciente.getTelefono().trim().equals(telefonoNormalizado)
                    && paciente.getId() != idActual) {
                return true;
            }
        }

        return false;
    }

    public void guardar() {
        duplicadoError = false;
        correoDuplicadoError = false;
        telefonoDuplicadoError = false;

        if (pacienteNuevo.getNombre() == null || pacienteNuevo.getNombre().trim().isEmpty()
                || dia == null || dia.isEmpty()
                || mes == null || mes.isEmpty()
                || anio == null || anio.isEmpty()
                || pacienteNuevo.getTelefono() == null || pacienteNuevo.getTelefono().trim().isEmpty()
                || pacienteNuevo.getTelefono().trim().length() < 10
                || pacienteNuevo.getGenero() == null || pacienteNuevo.getGenero().isEmpty()) {

            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        if (telefonoYaExiste(pacienteNuevo.getTelefono(), 0)) {
            telefonoDuplicadoError = true;
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
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            }
        } catch (Exception e) {
            Throwable causa = e;
            boolean manejado = false;

            while (causa != null) {
                if (causa.getMessage() != null && causa.getMessage().contains("Duplicate entry")) {
                    if (causa.getMessage().contains("correo")) {
                        correoDuplicadoError = true;
                    } else {
                        duplicadoError = true;
                    }

                    FacesContext.getCurrentInstance().validationFailed();
                    manejado = true;
                    break;
                }

                causa = causa.getCause();
            }

            if (!manejado) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el paciente."));
            }
        }
    }

    public void actualizarPaciente() {
        limpiarErroresEdicion();
        boolean hasErrors = false;

        if (pacienteEditar.getNombre() == null || pacienteEditar.getNombre().trim().isEmpty()) {
            errorNombreEditar = true;
            hasErrors = true;
        }

        if (diaEditar == null || diaEditar.isEmpty()
                || mesEditar == null || mesEditar.isEmpty()
                || anioEditar == null || anioEditar.isEmpty()) {
            errorFechaEditar = true;
            hasErrors = true;
        }

        if (pacienteEditar.getGenero() == null || pacienteEditar.getGenero().trim().isEmpty()) {
            errorGeneroEditar = true;
            hasErrors = true;
        }

        if (pacienteEditar.getTelefono() == null
                || pacienteEditar.getTelefono().trim().isEmpty()
                || pacienteEditar.getTelefono().trim().length() < 10) {
            errorTelefonoEditar = true;
            hasErrors = true;
        }

        if (hasErrors) {
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        if (telefonoYaExiste(pacienteEditar.getTelefono(), pacienteEditar.getId())) {
            telefonoDuplicadoEditar = true;
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
            Throwable causa = e;
            boolean manejado = false;

            while (causa != null) {
                if (causa.getMessage() != null && causa.getMessage().contains("Duplicate entry")) {
                    if (causa.getMessage().contains("correo")) {
                        correoDuplicadoEditar = true;
                    } else {
                        duplicadoEditar = true;
                    }

                    FacesContext.getCurrentInstance().validationFailed();
                    manejado = true;
                    break;
                }

                causa = causa.getCause();
            }

            if (!manejado) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar el paciente."));
            }
        }
    }

    public void limpiar() {
        pacienteNuevo = new PacienteEntity();
        dia = "";
        mes = "";
        anio = "";
        duplicadoError = false;
        correoDuplicadoError = false;
        telefonoDuplicadoError = false;
        actualizarDias();
    }

    public void limpiarErroresEdicion() {
        errorNombreEditar = false;
        errorFechaEditar = false;
        errorGeneroEditar = false;
        errorTelefonoEditar = false;
        duplicadoEditar = false;
        correoDuplicadoEditar = false;
        telefonoDuplicadoEditar = false;
    }

    public void abrirEdicion(int id) {
        limpiarErroresEdicion();

        pacienteEditar = facade.procesarConsultaPorId(id);

        diaEditar = "";
        mesEditar = "";
        anioEditar = "";

        if (pacienteEditar != null && pacienteEditar.getFechaNac() != null) {
            SimpleDateFormat sdfDia = new SimpleDateFormat("dd");
            SimpleDateFormat sdfMes = new SimpleDateFormat("MM");
            SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");

            diaEditar = sdfDia.format(pacienteEditar.getFechaNac());
            mesEditar = sdfMes.format(pacienteEditar.getFechaNac());
            anioEditar = sdfAnio.format(pacienteEditar.getFechaNac());
        }

        actualizarDiasEditar();

        org.primefaces.PrimeFaces.current().resetInputs("frmPrincipal:pnlEditar");
    }

    public void prepararEliminacion(int id) {
        pacienteEliminar = facade.procesarConsultaPorId(id);
    }

    public void eliminarPaciente() {
        try {
            if (pacienteEliminar == null || pacienteEliminar.getId() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se seleccionó ningún paciente"));
                return;
            }

            facade.procesarBajaLogica(pacienteEliminar.getId());
            cargarTodosActivos();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Paciente eliminado correctamente"));

            pacienteEliminar = new PacienteEntity();

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el paciente"));
        }
    }

    public void cancelarEdicion() {
        limpiarErroresEdicion();
    }

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

    public boolean isCorreoDuplicadoError() {
        return correoDuplicadoError;
    }

    public void setCorreoDuplicadoError(boolean correoDuplicadoError) {
        this.correoDuplicadoError = correoDuplicadoError;
    }

    public boolean isTelefonoDuplicadoError() {
        return telefonoDuplicadoError;
    }

    public void setTelefonoDuplicadoError(boolean telefonoDuplicadoError) {
        this.telefonoDuplicadoError = telefonoDuplicadoError;
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

    public PacienteEntity getPacienteEditar() {
        return pacienteEditar;
    }

    public void setPacienteEditar(PacienteEntity pacienteEditar) {
        this.pacienteEditar = pacienteEditar;
    }

    public PacienteEntity getPacienteEliminar() {
        return pacienteEliminar;
    }

    public void setPacienteEliminar(PacienteEntity pacienteEliminar) {
        this.pacienteEliminar = pacienteEliminar;
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

    public List<String> getListaDiasEditar() {
        return listaDiasEditar;
    }

    public void setListaDiasEditar(List<String> listaDiasEditar) {
        this.listaDiasEditar = listaDiasEditar;
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

    public boolean isErrorTelefonoEditar() {
        return errorTelefonoEditar;
    }

    public void setErrorTelefonoEditar(boolean errorTelefonoEditar) {
        this.errorTelefonoEditar = errorTelefonoEditar;
    }

    public boolean isDuplicadoEditar() {
        return duplicadoEditar;
    }

    public void setDuplicadoEditar(boolean duplicadoEditar) {
        this.duplicadoEditar = duplicadoEditar;
    }

    public boolean isCorreoDuplicadoEditar() {
        return correoDuplicadoEditar;
    }

    public void setCorreoDuplicadoEditar(boolean correoDuplicadoEditar) {
        this.correoDuplicadoEditar = correoDuplicadoEditar;
    }

    public boolean isTelefonoDuplicadoEditar() {
        return telefonoDuplicadoEditar;
    }

    public void setTelefonoDuplicadoEditar(boolean telefonoDuplicadoEditar) {
        this.telefonoDuplicadoEditar = telefonoDuplicadoEditar;
    }
}