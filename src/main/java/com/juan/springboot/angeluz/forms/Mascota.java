package com.juan.springboot.angeluz.forms;

import jakarta.persistence.*;

@Entity
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String tipoAnimal;
    private String raza;
    private int edad;
    private double peso;
    private String sexo;
    private boolean esterilizado;
    private String colorYMarcas;
    private String numeroMicrochip;



    // Necesidades especiales
    private String alimentacion;
    private String medicamentos;
    private String condicionesMedicas;
    private String necesidadesEspeciales;

    // Instrucciones para el cuidado
    private String horariosAlimentacion;
    private String paseos;
    private String actividades;
    private String contactoEmergencia;

    // Registro de salud
    private String nombreVeterinario;
    private String telefonoVeterinario;
    private String historialVacunacion;
    private String alergiasConocidas;
    private String condicionesMedicasPreexistentes;
    private String medicamentosActuales;
    private String informacionPulgasGarrapatas;
    // Getters y Setters

    @ManyToOne
    @JoinColumn(name = "entry_form_id") // Asegúrate de que este sea el nombre correcto de la columna en tu tabla 'mascota'
    private EntryForm entryForm;

    // Getter y Setter para entryForm
    public EntryForm getEntryForm() {
        return entryForm;
    }

    public void setEntryForm(EntryForm entryForm) {
        this.entryForm = entryForm;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoAnimal() {
        return tipoAnimal;
    }

    public void setTipoAnimal(String tipoAnimal) {
        this.tipoAnimal = tipoAnimal;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public boolean isEsterilizado() {
        return esterilizado;
    }

    public void setEsterilizado(boolean esterilizado) {
        this.esterilizado = esterilizado;
    }



    public String getInformacionPulgasGarrapatas() {
        return informacionPulgasGarrapatas;
    }

    public void setInformacionPulgasGarrapatas(String informacionPulgasGarrapatas) {
        this.informacionPulgasGarrapatas = informacionPulgasGarrapatas;
    }

    public String getMedicamentosActuales() {
        return medicamentosActuales;
    }

    public void setMedicamentosActuales(String medicamentosActuales) {
        this.medicamentosActuales = medicamentosActuales;
    }

    public String getCondicionesMedicasPreexistentes() {
        return condicionesMedicasPreexistentes;
    }

    public void setCondicionesMedicasPreexistentes(String condicionesMedicasPreexistentes) {
        this.condicionesMedicasPreexistentes = condicionesMedicasPreexistentes;
    }

    public String getAlergiasConocidas() {
        return alergiasConocidas;
    }

    public void setAlergiasConocidas(String alergiasConocidas) {
        this.alergiasConocidas = alergiasConocidas;
    }

    public String getHistorialVacunacion() {
        return historialVacunacion;
    }

    public void setHistorialVacunacion(String historialVacunacion) {
        this.historialVacunacion = historialVacunacion;
    }

    public String getTelefonoVeterinario() {
        return telefonoVeterinario;
    }

    public void setTelefonoVeterinario(String telefonoVeterinario) {
        this.telefonoVeterinario = telefonoVeterinario;
    }

    public String getNombreVeterinario() {
        return nombreVeterinario;
    }

    public void setNombreVeterinario(String nombreVeterinario) {
        this.nombreVeterinario = nombreVeterinario;
    }

    public String getContactoEmergencia() {
        return contactoEmergencia;
    }

    public void setContactoEmergencia(String contactoEmergencia) {
        this.contactoEmergencia = contactoEmergencia;
    }

    public String getActividades() {
        return actividades;
    }

    public void setActividades(String actividades) {
        this.actividades = actividades;
    }

    public String getPaseos() {
        return paseos;
    }

    public void setPaseos(String paseos) {
        this.paseos = paseos;
    }

    public String getHorariosAlimentacion() {
        return horariosAlimentacion;
    }

    public void setHorariosAlimentacion(String horariosAlimentacion) {
        this.horariosAlimentacion = horariosAlimentacion;
    }

    public String getNecesidadesEspeciales() {
        return necesidadesEspeciales;
    }

    public void setNecesidadesEspeciales(String necesidadesEspeciales) {
        this.necesidadesEspeciales = necesidadesEspeciales;
    }

    public String getCondicionesMedicas() {
        return condicionesMedicas;
    }

    public void setCondicionesMedicas(String condicionesMedicas) {
        this.condicionesMedicas = condicionesMedicas;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
    }

    public String getAlimentacion() {
        return alimentacion;
    }

    public void setAlimentacion(String alimentacion) {
        this.alimentacion = alimentacion;
    }

    public String getNumeroMicrochip() {
        return numeroMicrochip;
    }

    public void setNumeroMicrochip(String numeroMicrochip) {
        this.numeroMicrochip = numeroMicrochip;
    }

    public String getColorYMarcas() {
        return colorYMarcas;
    }

    public void setColorYMarcas(String colorYMarcas) {
        this.colorYMarcas = colorYMarcas;
    }

}