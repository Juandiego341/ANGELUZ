package com.juan.springboot.angeluz.authorization;

import com.juan.springboot.angeluz.forms.EntryForm;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class AutorizacionForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación One-to-One con EntryForm
    @OneToOne
    @JoinColumn(name = "entry_form_id")
    private EntryForm entryForm;

    // Autorizaciones y consentimientos
    private boolean autorizacionContactoVeterinario;
    private boolean consentimientoPrimerosAuxilios;
    private boolean autorizacionUsoImagen;
    private boolean declaracionResponsabilidad;
    private boolean autorizoCuidado;
    private boolean autorizoDecisionesMedicas;

    // Términos y Condiciones
    private String horariosEntradaSalida;
    private String tarifasYPago;
    private String politicasCancelacion;
    private String reglamentoInterno;

    // Firma y fecha
    private String firmaPropietario;
    private LocalDate fechaFirma;

    // getters y setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public EntryForm getEntryForm() {
        return entryForm;
    }
    public void setEntryForm(EntryForm entryForm) {
        this.entryForm = entryForm;
    }

    public boolean isAutorizacionContactoVeterinario() {
        return autorizacionContactoVeterinario;
    }
    public void setAutorizacionContactoVeterinario(boolean v) {
        this.autorizacionContactoVeterinario = v;
    }

    public boolean isConsentimientoPrimerosAuxilios() {
        return consentimientoPrimerosAuxilios;
    }
    public void setConsentimientoPrimerosAuxilios(boolean v) {
        this.consentimientoPrimerosAuxilios = v;
    }

    public boolean isAutorizacionUsoImagen() {
        return autorizacionUsoImagen;
    }
    public void setAutorizacionUsoImagen(boolean v) {
        this.autorizacionUsoImagen = v;
    }

    public boolean isDeclaracionResponsabilidad() {
        return declaracionResponsabilidad;
    }
    public void setDeclaracionResponsabilidad(boolean v) {
        this.declaracionResponsabilidad = v;
    }

    public boolean isAutorizoCuidado() {
        return autorizoCuidado;
    }
    public void setAutorizoCuidado(boolean v) {
        this.autorizoCuidado = v;
    }

    public boolean isAutorizoDecisionesMedicas() {
        return autorizoDecisionesMedicas;
    }
    public void setAutorizoDecisionesMedicas(boolean v) {
        this.autorizoDecisionesMedicas = v;
    }

    public String getHorariosEntradaSalida() {
        return horariosEntradaSalida;
    }
    public void setHorariosEntradaSalida(String s) {
        this.horariosEntradaSalida = s;
    }

    public String getTarifasYPago() {
        return tarifasYPago;
    }
    public void setTarifasYPago(String s) {
        this.tarifasYPago = s;
    }

    public String getPoliticasCancelacion() {
        return politicasCancelacion;
    }
    public void setPoliticasCancelacion(String s) {
        this.politicasCancelacion = s;
    }

    public String getReglamentoInterno() {
        return reglamentoInterno;
    }
    public void setReglamentoInterno(String s) {
        this.reglamentoInterno = s;
    }

    public String getFirmaPropietario() {
        return firmaPropietario;
    }
    public void setFirmaPropietario(String s) {
        this.firmaPropietario = s;
    }

    public LocalDate getFechaFirma() {
        return fechaFirma;
    }
    public void setFechaFirma(LocalDate d) {
        this.fechaFirma = d;
    }
}
