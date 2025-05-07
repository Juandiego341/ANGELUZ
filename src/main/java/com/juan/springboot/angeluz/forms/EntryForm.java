package com.juan.springboot.angeluz.forms;

import com.juan.springboot.angeluz.Admin.servicios.Servicio;
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
    private String cliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String metodoPago;
    private String estadoPago;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "servicio_id")
    private Servicio servicioSeleccionado;
    private boolean esReserva = false;
    @Column(nullable = false)
    private Double precioServicio = 0.0;
    private boolean completado = false;



    @Column(nullable = false)
    private Double valorTotal = 0.0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "entryform_productos",
            joinColumns = @JoinColumn(name = "entryform_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productosSeleccionados = new ArrayList<>();

    @OneToOne(mappedBy = "entryForm", cascade = CascadeType.ALL, orphanRemoval = true)
    private AutorizacionForm autorizacionForm;

    @OneToMany(mappedBy = "entryForm", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Mascota> mascotas = new ArrayList<>();


    // Constructor
    public EntryForm() {
        this.fechaInicio = LocalDate.now();
        this.precioServicio = 0.0;
        this.valorTotal = 0.0;
        this.productosSeleccionados = new ArrayList<>();
        this.mascotas = new ArrayList<>();
    }

    // Métodos de cálculo
    @Transient
    public Double getTotalProductos() {
        if (productosSeleccionados == null || productosSeleccionados.isEmpty()) {
            return 0.0;
        }
        double suma = 0.0;
        for (Producto producto : productosSeleccionados) {
            if (producto != null && producto.getPrecio() != null) {
                suma += producto.getPrecio();
            }
        }
        return suma;
    }

    @PrePersist
    @PreUpdate
    public void calcularValorTotal() {
        double totalProductos = getTotalProductos();
        double precioServicioActual = getPrecioServicio();
        this.valorTotal = totalProductos + precioServicioActual;
    }

    @Transient
    public boolean isCompletado() {
        return fechaFin != null && !fechaFin.isAfter(LocalDate.now());
    }

    // Métodos helper para productos y mascotas
    public void agregarProducto(Producto producto) {
        if (productosSeleccionados == null) {
            productosSeleccionados = new ArrayList<>();
        }
        if (producto != null) {
            productosSeleccionados.add(producto);
            calcularValorTotal();
        }
    }

    public void removerProducto(Producto producto) {
        if (productosSeleccionados != null && producto != null) {
            productosSeleccionados.remove(producto);
            calcularValorTotal();
        }
    }

    public void agregarMascota(Mascota mascota) {
        if (mascotas == null) {
            mascotas = new ArrayList<>();
        }
        if (mascota != null) {
            mascotas.add(mascota);
            mascota.setEntryForm(this);
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEsReserva() {
        return esReserva;
    }

    public void setEsReserva(boolean esReserva) {
        this.esReserva = esReserva;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
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

    public Servicio getServicioSeleccionado() {
        return servicioSeleccionado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public void setServicioSeleccionado(Servicio servicioSeleccionado) {
        this.servicioSeleccionado = servicioSeleccionado;
        if (servicioSeleccionado != null && servicioSeleccionado.getPrecio() != null) {
            this.precioServicio = servicioSeleccionado.getPrecio();
        }
        calcularValorTotal();
    }

    public Double getPrecioServicio() {
        if (servicioSeleccionado != null && servicioSeleccionado.getPrecio() != null) {
            this.precioServicio = servicioSeleccionado.getPrecio();
            return this.precioServicio;
        }
        return this.precioServicio != null ? this.precioServicio : 0.0;
    }

    public void setPrecioServicio(Double precioServicio) {
        this.precioServicio = precioServicio != null ? precioServicio : 0.0;
        calcularValorTotal();
    }

    public Double getValorTotal() {
        if (valorTotal == null) {
            calcularValorTotal();
        }
        return valorTotal != null ? valorTotal : 0.0;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal != null ? valorTotal : 0.0;
    }

    public List<Producto> getProductosSeleccionados() {
        if (productosSeleccionados == null) {
            productosSeleccionados = new ArrayList<>();
        }
        return productosSeleccionados;
    }

    public void setProductosSeleccionados(List<Producto> productosSeleccionados) {
        this.productosSeleccionados = productosSeleccionados != null ? productosSeleccionados : new ArrayList<>();
        calcularValorTotal();
    }

    public List<Mascota> getMascotas() {
        if (mascotas == null) {
            mascotas = new ArrayList<>();
        }
        return mascotas;
    }

    public void setMascotas(List<Mascota> mascotas) {
        this.mascotas = mascotas != null ? mascotas : new ArrayList<>();
    }

    public AutorizacionForm getAutorizacionForm() {
        return autorizacionForm;
    }

    public void setAutorizacionForm(AutorizacionForm autorizacionForm) {
        this.autorizacionForm = autorizacionForm;
    }
}