package com.juan.springboot.angeluz.forms;

        import jakarta.persistence.*;
        import java.time.LocalDate;
        import java.util.List;

@Entity
        public class EntryForm {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

            private String nombrePropietario;
            private String cedulaCiudadania;
            private String correo;
            private String celular;
            private String queVaASer;

            private LocalDate fechaInicio; // Fecha de inicio del alojamiento
            private LocalDate fechaFin;    // Fecha de fin del alojamiento

            @OneToMany(mappedBy = "entryForm", cascade = CascadeType.ALL)
            private List<Pet> mascotas;

            // Getters y Setters
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

            public List<Pet> getMascotas() {
                return mascotas;
            }

            public void setMascotas(List<Pet> mascotas) {
                this.mascotas = mascotas;
            }
        }