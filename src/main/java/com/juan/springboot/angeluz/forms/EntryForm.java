package com.juan.springboot.angeluz.forms;

import com.juan.springboot.angeluz.authorization.AutorizacionForm;
import com.juan.springboot.angeluz.shop.Producto;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EntryForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    private String nombrePropietario;
    private String cedulaCiudadania;
    private String correo;
    private String celular;
    private String queVaASer;
    private String direccion;

    private LocalDate fechaInicio; // Fecha de inicio del alojamiento
    private LocalDate fechaFin;    // Fecha de fin del alojamiento
    private Double valorTotal;

    @ManyToMany
    @JoinTable(
            name = "entryform_productos",
            joinColumns = @JoinColumn(name = "entryform_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productosSeleccionados;
    @OneToOne(mappedBy = "entryForm", cascade = CascadeType.ALL, orphanRemoval = true)
    private AutorizacionForm autorizacionForm;


    public AutorizacionForm getAutorizacionForm() {
        return autorizacionForm;
    }

    public void setAutorizacionForm(AutorizacionForm autorizacionForm) {
        this.autorizacionForm = autorizacionForm;
    }

    @OneToMany(mappedBy = "entryForm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Mascota> mascotas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getCedulaCiudadania() {
        return cedulaCiudadania;
    }

    public void setCedulaCiudadania(String cedulaCiudadania) {
        this.cedulaCiudadania = cedulaCiudadania;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getQueVaASer() {
        return queVaASer;
    }

    public void setQueVaASer(String queVaASer) {
        this.queVaASer = queVaASer;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<Mascota> getMascotas() {
        return mascotas;
    }

    public void setMascotas(List<Mascota> mascotas) {
        this.mascotas = mascotas;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<Producto> getProductosSeleccionados() {
        return productosSeleccionados;
    }

    public void setProductosSeleccionados(List<Producto> productosSeleccionados) {
        this.productosSeleccionados = productosSeleccionados;
    }
}