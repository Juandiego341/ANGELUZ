package com.juan.springboot.angeluz.forms.Controller;

import com.juan.springboot.angeluz.Admin.servicios.Servicio;
import com.juan.springboot.angeluz.Admin.servicios.ServicioRepositorio;
import com.juan.springboot.angeluz.authorization.AutorizacionFormRepository;
import com.juan.springboot.angeluz.forms.EntryForm;
import com.juan.springboot.angeluz.forms.EntryFormRepository;
import com.juan.springboot.angeluz.forms.Mascota;
import com.juan.springboot.angeluz.shop.Producto;
import com.juan.springboot.angeluz.shop.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@SessionAttributes("entryForm")
public class EntryFormController {
    @Autowired
    private AutorizacionFormRepository autorizacionRepo;
    @Autowired
    private ServicioRepositorio servicioRepository;
    @Autowired
    private EntryFormRepository entryFormRepository;
    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/moderador/EntradasYSalidas/editar/{id}")
    public String editarRegistro(@PathVariable Long id, Model model) {
        EntryForm entryForm = entryFormRepository.findById(id).orElse(null);
        if (entryForm == null) {
            model.addAttribute("error", "No se encontró el registro.");
            return "redirect:/moderador/EntradasYSalidas";
        }
        // Aquí entryForm.getProductosSeleccionados() debe traer los productos/servicios previos
        double total = 0.0;
        if (entryForm.getProductosSeleccionados() != null) {
            total = entryForm.getProductosSeleccionados()
                    .stream()
                    .mapToDouble(p -> p.getPrecio() != null ? p.getPrecio() : 0.0)
                    .sum();
        }
        model.addAttribute("entryForm", entryForm);
        model.addAttribute("serviciosDisponibles", servicioRepository.findAll());
        model.addAttribute("total", total);
        return "moderador/EditarRegistro";
    }


    @ModelAttribute("entryForm")
    public EntryForm setupEntryForm() {
        return new EntryForm();
    }

    @GetMapping("/moderador/checkout")
    public String mostrarFormularioCheckout(Model model) {
        if (!model.containsAttribute("entryForm")) {
            EntryForm entryForm = new EntryForm();
            entryForm.setQueVaASer(""); // Valor predeterminado
            model.addAttribute("entryForm", entryForm);
        }

        // Traer los servicios desde la base de datos
        List<Servicio> servicios = servicioRepository.findAll();
        model.addAttribute("servicios", servicios);

        return "checkout";
    }

    // Procesar formulario de checkout
    @PostMapping("/moderador/checkout")
    public String procesarFormularioCheckout(
            @ModelAttribute("entryForm") EntryForm entryForm,
            @RequestParam(value = "servicioSeleccionadoId", required = false) Long servicioSeleccionadoId,
            Model model) {
        if (servicioSeleccionadoId != null) {
            Servicio servicioSeleccionado = servicioRepository.findById(servicioSeleccionadoId).orElse(null);
            if (servicioSeleccionado != null) {
                if (entryForm.getProductosSeleccionados() == null) {
                    entryForm.setProductosSeleccionados(new ArrayList<>());
                }
                Producto productoServicio = new Producto();
                productoServicio.setId(servicioSeleccionado.getId());
                productoServicio.setNombre(servicioSeleccionado.getNombre());
                productoServicio.setPrecio(servicioSeleccionado.getPrecio());
                entryForm.getProductosSeleccionados().add(productoServicio);
                entryForm.setQueVaASer(servicioSeleccionado.getNombre());
            }
        }
        model.addAttribute("entryForm", entryForm);
        return "redirect:/moderador/mascotas";
    }
    // Mostrar formulario de mascotas
    @GetMapping("/moderador/mascotas")
    public String mostrarFormularioMascotas(Model model) {
        EntryForm entryForm = (EntryForm) model.asMap().get("entryForm");

        if (entryForm == null) {
            return "redirect:/moderador/checkout"; // Redirige si no está en la sesión
        }

        if (entryForm.getMascotas() == null || entryForm.getMascotas().isEmpty()) {
            entryForm.setMascotas(new ArrayList<>());
            entryForm.getMascotas().add(new Mascota());
        }

        model.addAttribute("entryForm", entryForm);
        return "mascotas";
    }

    // Guardar formulario completo
    @PostMapping("/moderador/mascotas")
    public String guardarFormularioCompleto(
            @ModelAttribute("entryForm") EntryForm entryForm,
            RedirectAttributes redirectAttributes) {

        // Establecer la relación para las mascotas
        for (Mascota mascota : entryForm.getMascotas()) {
            mascota.setEntryForm(entryForm);
        }

        // ¡NO GUARDAR AÚN!
        // entryFormRepository.save(entryForm);

        redirectAttributes.addAttribute("entryFormId", entryForm.getId());
        return "redirect:/moderador/autorizacion";
    }



    // Listar registros
    @GetMapping("/moderador/EntradasYSalidas")
    public String listarRegistros(Model model) {
        List<EntryForm> registros = entryFormRepository.findAll();
        model.addAttribute("registros", registros);
        return "moderador/EntradasYSalidas";
    }

    // Buscar registro por ID
    @GetMapping("/moderador/EntradasYSalidas/buscar")
    public String buscarRegistroPorId(@RequestParam("id") Long id, Model model) {
        Optional<EntryForm> registro = entryFormRepository.findById(id);
        if (registro.isPresent()) {
            model.addAttribute("registro", registro.get());
        } else {
            model.addAttribute("error", "No se encontró un registro con el ID proporcionado.");
        }
        return "moderador/EntradasYSalidas";
    }


    // Java
    @PostMapping("/moderador/EntradasYSalidas/editar/{id}")
    public String guardarEdicion(
            @PathVariable Long id,
            @ModelAttribute("entryForm") EntryForm actualizado, // Datos del formulario
            RedirectAttributes redirectAttributes, // Para mensajes flash
            Model model) { // Para volver a mostrar en caso de error
        try {
            // Carga la entidad existente desde la BD
            EntryForm existente = entryFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Registro no encontrado con ID: " + id));

            // *** COPIAR TODOS LOS CAMPOS EDITABLES ***
            existente.setNombrePropietario(actualizado.getNombrePropietario());
            existente.setCedulaCiudadania(actualizado.getCedulaCiudadania());
            existente.setCorreo(actualizado.getCorreo());
            existente.setCelular(actualizado.getCelular());
            existente.setQueVaASer(actualizado.getQueVaASer());

            if (actualizado.getMascotas() != null && existente.getMascotas() != null) {

                existente.setMascotas(actualizado.getMascotas()); // ¡CUIDADO! Esto puede tener efectos secundarios con JPA/Hibernate si las mascotas son entidades separadas. Se necesita un merge adecuado.
                // Asegurar relación bidireccional si existe
                for(Mascota m : existente.getMascotas()) { m.setEntryForm(existente); }
            }


            // --- Manejo de Autorización ---
            if (actualizado.getAutorizacionForm() != null) {

                if (existente.getAutorizacionForm() != null) {

                    existente.getAutorizacionForm().setAutorizacionContactoVeterinario(actualizado.getAutorizacionForm().isAutorizacionContactoVeterinario());
                    existente.getAutorizacionForm().setConsentimientoPrimerosAuxilios(actualizado.getAutorizacionForm().isConsentimientoPrimerosAuxilios());

                    existente.getAutorizacionForm().setFechaFirma(actualizado.getAutorizacionForm().getFechaFirma());
                } else if (actualizado.getAutorizacionForm() != null) {

                    actualizado.getAutorizacionForm().setEntryForm(existente);
                    existente.setAutorizacionForm(actualizado.getAutorizacionForm());
                }
            } else {

                existente.setAutorizacionForm(null);
            }




            entryFormRepository.save(existente); // Guarda la entidad existente actualizada
            redirectAttributes.addFlashAttribute("successMessage", "Registro actualizado correctamente.");
            return "redirect:/moderador/EntradasYSalidas"; // Redirige a la lista

        } catch (Exception e) {

            System.err.println("Error al guardar edición: " + e.getMessage());
            e.printStackTrace(); // Para depuración

            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar: " + e.getMessage());

            double total = 0.0;
            if (actualizado.getProductosSeleccionados() != null) {
                total = actualizado.getProductosSeleccionados()
                        .stream()
                        .mapToDouble(p -> p.getPrecio() != null ? p.getPrecio() : 0.0)
                        .sum();
            }
            model.addAttribute("entryForm", actualizado); // Devuelve los datos que el usuario intentó guardar
            model.addAttribute("serviciosDisponibles", servicioRepository.findAll());
            model.addAttribute("total", total);
            model.addAttribute("errorMessage", "Error al guardar. Verifique los datos."); // Mensaje para mostrar en la vista
            return "moderador/EditarRegistro";
        }
    }

    // Eliminar registro
    @GetMapping("/moderador/EntradasYSalidas/eliminar/{id}")
    public String eliminarRegistro(@PathVariable("id") Long id) {
        entryFormRepository.deleteById(id);
        return "redirect:/moderador/EntradasYSalidas";
    }


    @GetMapping("/moderador/EntradasYSalidas/filtrar")
    public String filtrarPorFechaYBusqueda(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "search", required = false) String search,
            Model model) {

        List<EntryForm> todos = entryFormRepository.findAll();

        List<EntryForm> filtradosPorFecha = todos.stream()
                .filter(r -> {
                    if (startDate != null && endDate != null && r.getFechaInicio() != null && r.getFechaFin() != null) {
                        return !r.getFechaInicio().isAfter(endDate) && !r.getFechaFin().isBefore(startDate);
                    }
                    return true;
                })
                .toList();

        List<EntryForm> filtrados = filtradosPorFecha.stream()
                .filter(r -> {
                    if (search == null || search.isBlank()) return true;
                    String s = search.toLowerCase();
                    return String.valueOf(r.getId()).contains(s)
                            || (r.getNombrePropietario() != null && r.getNombrePropietario().toLowerCase().contains(s))
                            || (r.getCorreo() != null && r.getCorreo().toLowerCase().contains(s));

                })
                .toList();

        List<EntryForm> registrosConFecha = filtrados.stream()
                .filter(r -> r.getFechaInicio() != null && r.getFechaFin() != null)
                .toList();
        List<EntryForm> registrosSinFecha = filtrados.stream()
                .filter(r -> r.getFechaInicio() == null || r.getFechaFin() == null)
                .toList();

        model.addAttribute("registrosConFecha", registrosConFecha);
        model.addAttribute("registrosSinFecha", registrosSinFecha);
        model.addAttribute("search", search);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "moderador/EntradasYSalidas";
    }



    @GetMapping("/moderador/seleccionar-productos")
    public String seleccionarProductos(Model model, @SessionAttribute("entryForm") EntryForm entryForm) {
        model.addAttribute("entryForm", entryForm);
        model.addAttribute("productos", productoRepository.findAll());
        return "moderador/SeleccionarProductos";
    }
    @PostMapping("/moderador/guardar-productos")
    public String guardarProductosSeleccionados(@RequestParam("productoIds") List<Long> productoIds,
                                                @SessionAttribute("entryForm") EntryForm entryForm,
                                                Model model) {
        List<Producto> productosEncontrados = productoRepository.findAllById(productoIds);

        if (productosEncontrados.size() != productoIds.size()) {
            model.addAttribute("errorProductos", "Uno o más de los productos seleccionados no son válidos. Por favor, inténtelo de nuevo.");
            model.addAttribute("productos", productoRepository.findAll());
            return "moderador/SeleccionarProductos";
        }

        // Añadir los nuevos productos a la lista existente
        if (entryForm.getProductosSeleccionados() == null) {
            entryForm.setProductosSeleccionados(new ArrayList<>());
        }
        entryForm.getProductosSeleccionados().addAll(productosEncontrados);

        return "redirect:/resumen-total";
    }

    @GetMapping("/resumen-total")
    public String mostrarResumenTotal(Model model, @SessionAttribute("entryForm") EntryForm entryForm) {
        System.out.println("--- MOSTRAR RESUMEN TOTAL ---");
        System.out.println("EntryForm: " + entryForm);

        double total = 0.0;
        if (entryForm.getProductosSeleccionados() != null && !entryForm.getProductosSeleccionados().isEmpty()) {
            total = entryForm.getProductosSeleccionados().stream()
                    .map(Producto::getPrecio)
                    .reduce(0.0, Double::sum);
        }

        model.addAttribute("total", total);
        model.addAttribute("entryForm", entryForm);
        return "Total";
    }
    @PostMapping("/resumen-total")
    public String finalizarRegistro(@SessionAttribute("entryForm") EntryForm entryForm,
                                    SessionStatus status) {
        // Establecer la relación para la autorización (asegurarse de que esté asociada)
        if (entryForm.getAutorizacionForm() != null) {
            entryForm.getAutorizacionForm().setEntryForm(entryForm);
        }

        // Guardar el EntryForm completo con todas sus relaciones
        entryFormRepository.save(entryForm);
        status.setComplete(); // Limpia los atributos de sesión
        return "redirect:/moderador/confirmacion";
    }

    @PostMapping("/moderador/eliminar-producto-por-id")
    public String eliminarProductoPorId(@RequestParam("productoId") Long productoId,
                                        @SessionAttribute("entryForm") EntryForm entryForm,
                                        Model model) {
        if (entryForm.getProductosSeleccionados() != null) {
            entryForm.getProductosSeleccionados().removeIf(producto -> producto.getId().equals(productoId));
        }
        return "redirect:/resumen-total";
    }
    @PostMapping("/moderador/agregar-servicio")
    public String agregarServicio(@RequestParam(value = "servicioId", required = false) Long servicioId,
                                  @SessionAttribute("entryForm") EntryForm entryForm,
                                  Model model) {
        if (servicioId != null) {
            Servicio servicioSeleccionado = servicioRepository.findById(servicioId).orElse(null);
            if (servicioSeleccionado != null) {
                if (entryForm.getProductosSeleccionados() == null) {
                    entryForm.setProductosSeleccionados(new ArrayList<>());
                }
                Producto productoServicio = new Producto();
                productoServicio.setId(servicioSeleccionado.getId());
                productoServicio.setNombre(servicioSeleccionado.getNombre());
                productoServicio.setPrecio(servicioSeleccionado.getPrecio());
                entryForm.getProductosSeleccionados().add(productoServicio);
            }
        }
        return "redirect:/resumen-total";
    }

    @ModelAttribute("serviciosDisponibles")
    public List<Servicio> getServiciosDisponibles() {
        return servicioRepository.findAll();
    }
    // En EntryFormController.java

    @PostMapping("/moderador/EntradasYSalidas/editar/{id}/agregar-servicio")
    public String agregarServicioAEditado(@PathVariable Long id,
                                          @RequestParam("servicioId") Long servicioId, // ID del Servicio/Producto a agregar
                                          RedirectAttributes redirectAttributes) {
        // Busca la entidad principal que se está editando
        Optional<EntryForm> entryOpt = entryFormRepository.findById(id);
        // Busca el servicio/producto que se quiere agregar
        // Asumiendo que serviciosDisponibles son los que puedes agregar como Producto
        Optional<Servicio> servicioOpt = servicioRepository.findById(servicioId);

        if (entryOpt.isPresent() && servicioOpt.isPresent()) {
            EntryForm entryForm = entryOpt.get();
            Servicio servicio = servicioOpt.get();


            Producto productoAAgregar = new Producto();
            // Copia las propiedades relevantes del Servicio al Producto
            productoAAgregar.setNombre(servicio.getNombre());
            productoAAgregar.setPrecio(servicio.getPrecio());


            if (entryForm.getProductosSeleccionados() == null) {
                entryForm.setProductosSeleccionados(new ArrayList<>());
            }

            // Opcional: Evitar duplicados (basado en el ID del servicio original o nombre)
            Long idServicioOriginal = servicio.getId();
            boolean alreadyExists = entryForm.getProductosSeleccionados().stream()
                    // Compara con una propiedad que identifique unívocamente el servicio/producto
                    .anyMatch(p -> p.getNombre() != null && p.getNombre().equals(servicio.getNombre())); // O usa ID si lo mapeas

            if (!alreadyExists) {
                // Asegúrate de establecer la relación si Producto es una entidad persistente
                // productoAAgregar.setEntryForm(entryForm); // Si hay relación bidireccional
                entryForm.getProductosSeleccionados().add(productoAAgregar);
                entryFormRepository.save(entryForm); // Guarda la entidad actualizada
                redirectAttributes.addFlashAttribute("successMessage", "Servicio agregado.");
            } else {
                redirectAttributes.addFlashAttribute("warningMessage", "Este servicio ya fue agregado.");
            }

        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Registro o Servicio no encontrado.");
        }
        // Redirige de vuelta a la PÁGINA DE EDICIÓN
        return "redirect:/moderador/EntradasYSalidas/editar/" + id;
    }

    @PostMapping("/moderador/EntradasYSalidas/editar/{registroId}/eliminar-producto")
    public String eliminarProductoDeEditado(@PathVariable Long registroId, // Renombrado para claridad
                                            @RequestParam("productoId") Long productoId, // ID del Producto a eliminar DE LA LISTA
                                            RedirectAttributes redirectAttributes) {

        Optional<EntryForm> entryOpt = entryFormRepository.findById(registroId);

        if (entryOpt.isPresent()) {
            EntryForm entryForm = entryOpt.get();
            boolean removed = false;
            if (entryForm.getProductosSeleccionados() != null) {

                removed = entryForm.getProductosSeleccionados().removeIf(p -> p.getId() != null && p.getId().equals(productoId));
            }

            if (removed) {
                entryFormRepository.save(entryForm);
                redirectAttributes.addFlashAttribute("successMessage", "Producto eliminado.");
            } else {
                redirectAttributes.addFlashAttribute("warningMessage", "Producto no encontrado en la lista.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Registro no encontrado.");
        }

        return "redirect:/moderador/EntradasYSalidas/editar/" + registroId;
    }
}

