package mx.sipsi.ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.sipsi.entity.PacienteEntity;
import mx.sipsi.negocio.delegate.PacienteDelegate;
import mx.sipsi.persistence.persistence.PacientePersistence;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Named("pacienteBean")
@ViewScoped
public class PacienteBean implements Serializable {

    private PacienteEntity pacienteNuevo;
    private String dia, mes, anio;
    private List<String> listaDias;
    private List<String> listaAnios;

    private boolean duplicadoError = false;

    private PacienteDelegate delegate = new PacienteDelegate();
    private PacientePersistence persistence = new PacientePersistence();

    @PostConstruct
    public void init() {
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        listaAnios = new ArrayList<>();
        for (int i = anioActual; i >= 1926; i--) {
            listaAnios.add(String.valueOf(i));
        }
        limpiar();
    }

    public void actualizarDias() {
        int maxDias = 31;
        if (mes != null && !mes.isEmpty()) {
            int mesInt = Integer.parseInt(mes);
            int anioInt = (anio != null && !anio.isEmpty()) ? Integer.parseInt(anio) : 2026;
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, anioInt);
            cal.set(Calendar.MONTH, mesInt - 1);
            maxDias = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        generarDias(maxDias);
        if (dia != null && !dia.isEmpty() && Integer.parseInt(dia) > maxDias) {
            this.dia = String.format("%02d", maxDias);
        }
    }

    private void generarDias(int max) {
        listaDias = new ArrayList<>();
        for (int i = 1; i <= max; i++) {
            listaDias.add(String.format("%02d", i));
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

            if (persistence.checkDuplicate(pacienteNuevo.getNombre(), fechaNac)) {
                duplicadoError = true;
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }

            if (pacienteNuevo.getCorreo() != null && pacienteNuevo.getCorreo().trim().isEmpty()) {
                pacienteNuevo.setCorreo(null);
            }

            delegate.registrarPaciente(pacienteNuevo);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Paciente registrado"));

            limpiar();
            org.primefaces.PrimeFaces.current().executeScript("PF('dlgRegistro').hide()");

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error en BD: " + e.getMessage()));
        }
    }

    public void limpiar() {
        pacienteNuevo = new PacienteEntity();
        dia = ""; mes = ""; anio = "";
        duplicadoError = false;
        actualizarDias();
    }

    public PacienteEntity getPacienteNuevo() { return pacienteNuevo; }
    public void setPacienteNuevo(PacienteEntity p) { this.pacienteNuevo = p; }
    public String getDia() { return dia; }
    public void setDia(String d) { this.dia = d; }
    public String getMes() { return mes; }
    public void setMes(String m) { this.mes = m; }
    public String getAnio() { return anio; }
    public void setAnio(String a) { this.anio = a; }
    public List<String> getListaDias() { return listaDias; }
    public List<String> getListaAnios() { return listaAnios; }
    public boolean isDuplicadoError() { return duplicadoError; }
}