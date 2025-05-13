package com.juan.springboot.angeluz.forms.Controller;

import com.juan.springboot.angeluz.Admin.servicios.Servicio;
import com.juan.springboot.angeluz.Admin.servicios.ServicioRepositorio;
import com.juan.springboot.angeluz.authorization.AutorizacionForm;
import com.juan.springboot.angeluz.authorization.AutorizacionFormRepository;
import com.juan.springboot.angeluz.forms.EntryForm;
import com.juan.springboot.angeluz.forms.EntryFormRepository;
import com.juan.springboot.angeluz.forms.Mascota;
import com.juan.springboot.angeluz.shop.Producto;
import com.juan.springboot.angeluz.shop.ProductoRepository;
import com.juan.springboot.angeluz.shop.ProductoSeleccionado;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
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


    @Transactional
    @GetMapping("/moderador/EntradasYSalidas/editar/{id}")
    public String editarRegistro(@PathVariable Long id, Model model) {
        try {
            EntryForm entryForm = entryFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

            // Cargar el servicio seleccionado si existe
            if (entryForm.getServicioSeleccionado() != null) {
                Servicio servicio = servicioRepository.findById(entryForm.getServicioSeleccionado().getId())
                        .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
                entryForm.setServicioSeleccionado(servicio);
                entryForm.setQueVaASer(servicio.getNombre());
            }

            // Calcular el total incluyendo servicio y productos
            double total = 0.0;

            // Sumar el precio del servicio seleccionado
            if (entryForm.getServicioSeleccionado() != null) {
                total += entryForm.getServicioSeleccionado().getPrecio();
            }

            // Sumar los precios de los productos
            if (entryForm.getProductosSeleccionados() != null) {
                total += entryForm.getProductosSeleccionados()
                        .stream()
                        .mapToDouble(p -> p.getPrecio() != null ? p.getPrecio() : 0.0)
                        .sum();
            }


            if (entryForm.getMascotas() == null) {
                entryForm.setMascotas(new ArrayList<>());
            }
            if (entryForm.getProductosSeleccionados() == null) {
                entryForm.setProductosSeleccionados(new ArrayList<>());
            }
            if (entryForm.getAutorizacionForm() == null) {
                entryForm.setAutorizacionForm(new AutorizacionForm());
            }

            model.addAttribute("entryForm", entryForm);
            model.addAttribute("serviciosDisponibles", servicioRepository.findAll());
            model.addAttribute("productosDisponibles", productoRepository.findAll());
            model.addAttribute("total", total);

            return "moderador/EditarRegistro";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar el registro: " + e.getMessage());
            return "redirect:/moderador/EntradasYSalidas";
        }
    }

    @ModelAttribute("entryForm")
    public EntryForm setupEntryForm() {
        return new EntryForm();
    }

    @GetMapping("/moderador/checkout")
    public String mostrarFormularioCheckout(Model model) {
        if (!model.containsAttribute("entryForm")) {
            EntryForm entryForm = new EntryForm();
            entryForm.setQueVaASer("");
            model.addAttribute("entryForm", entryForm);
        }

        List<Servicio> servicios = servicioRepository.findAll();
        model.addAttribute("servicios", servicios);
        EntryForm nuevoForm = new EntryForm();
        nuevoForm.setProductosSeleccionados(new ArrayList<>());
        nuevoForm.setMascotas(new ArrayList<>());
        nuevoForm.setAutorizacionForm(new AutorizacionForm());

        model.addAttribute("entryForm", nuevoForm);

        return "checkout";
    }

    @PostMapping("/moderador/checkout")
    public String procesarFormularioCheckout(
            @ModelAttribute("entryForm") EntryForm entryForm,
            @RequestParam(value = "servicioSeleccionadoId", required = false) Long servicioSeleccionadoId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            if (servicioSeleccionadoId != null) {
                // Obtener el servicio seleccionado
                Servicio servicioSeleccionado = servicioRepository.findById(servicioSeleccionadoId)
                        .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

                // Establecer el servicio directamente en el entryForm
                entryForm.setServicioSeleccionado(servicioSeleccionado);
                entryForm.setQueVaASer(servicioSeleccionado.getNombre());

                model.addAttribute("entryForm", entryForm);
                return "redirect:/moderador/mascotas";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Debe seleccionar un servicio");
                return "redirect:/moderador/checkout";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al procesar el checkout: " + e.getMessage());
            return "redirect:/moderador/checkout";
        }
    }

    @GetMapping("/moderador/mascotas")
    public String mostrarFormularioMascotas(Model model) {
        EntryForm entryForm = (EntryForm) model.asMap().get("entryForm");

        if (entryForm == null) {
            return "redirect:/moderador/checkout";
        }

        if (entryForm.getMascotas() == null || entryForm.getMascotas().isEmpty()) {
            entryForm.setMascotas(new ArrayList<>());
            entryForm.getMascotas().add(new Mascota());
        }

        model.addAttribute("entryForm", entryForm);
        return "mascotas";
    }

    @PostMapping("/moderador/mascotas")
    public String guardarFormularioCompleto(
            @ModelAttribute("entryForm") EntryForm entryForm,
            RedirectAttributes redirectAttributes) {

        for (Mascota mascota : entryForm.getMascotas()) {
            mascota.setEntryForm(entryForm);
        }


        redirectAttributes.addAttribute("entryFormId", entryForm.getId());
        return "redirect:/moderador/autorizacion";
    }


    @GetMapping("/moderador/EntradasYSalidas")
    public String listarRegistros(Model model) {
        List<EntryForm> registros = entryFormRepository.findAll();
        model.addAttribute("registros", registros);
        return "moderador/EntradasYSalidas";
    }

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

        // Sumar el precio del servicio si existe
        if (entryForm.getServicioSeleccionado() != null) {
            total += entryForm.getServicioSeleccionado().getPrecio();
        }

        // Sumar los productos
        if (entryForm.getProductosSeleccionados() != null && !entryForm.getProductosSeleccionados().isEmpty()) {
            total += entryForm.getProductosSeleccionados().stream()
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

        if (entryForm.getAutorizacionForm() != null) {
            entryForm.getAutorizacionForm().setEntryForm(entryForm);
        }


        entryFormRepository.save(entryForm);
        status.setComplete();
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
    public String agregarServicio(
            @RequestParam(value = "servicioId", required = false) Long servicioId,
            @SessionAttribute("entryForm") EntryForm entryForm,
            RedirectAttributes redirectAttributes) {
        try {
            if (servicioId != null) {
                Servicio servicioSeleccionado = servicioRepository.findById(servicioId)
                        .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
                Optional<Producto> productoExistente = productoRepository.findByNombre(
                        servicioSeleccionado.getNombre());

                Producto productoServicio = productoExistente.orElseGet(() -> {
                    Producto nuevo = new Producto();
                    nuevo.setNombre(servicioSeleccionado.getNombre());
                    nuevo.setPrecio(servicioSeleccionado.getPrecio());
                    return productoRepository.save(nuevo);
                });

                if (entryForm.getProductosSeleccionados() == null) {
                    entryForm.setProductosSeleccionados(new ArrayList<>());
                }

                boolean exists = entryForm.getProductosSeleccionados().stream()
                        .anyMatch(p -> p.getId().equals(productoServicio.getId()));

                if (!exists) {
                    entryForm.getProductosSeleccionados().add(productoServicio);
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Servicio agregado correctamente");
                } else {
                    redirectAttributes.addFlashAttribute("warningMessage",
                            "Este servicio ya está agregado");
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al agregar el servicio: " + e.getMessage());
        }

        return "redirect:/resumen-total";
    }

    @ModelAttribute("serviciosDisponibles")
    public List<Servicio> getServiciosDisponibles() {
        return servicioRepository.findAll();
    }

    @PostMapping("/moderador/EntradasYSalidas/editar/{id}/agregar-servicio")
    public String agregarServicioAEditado(@PathVariable Long id,
                                          @RequestParam("servicioId") Long servicioId,
                                          RedirectAttributes redirectAttributes) {
        try {
            EntryForm entryForm = entryFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

            Servicio servicio = servicioRepository.findById(servicioId)
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

            Producto producto = productoRepository.findByNombre(servicio.getNombre())
                    .orElseGet(() -> {
                        Producto nuevo = new Producto();
                        nuevo.setNombre(servicio.getNombre());
                        nuevo.setPrecio(servicio.getPrecio());
                        return productoRepository.save(nuevo);
                    });

            if (entryForm.getProductosSeleccionados() == null) {
                entryForm.setProductosSeleccionados(new ArrayList<>());
            }

            entryForm.getProductosSeleccionados().add(producto);
            entryFormRepository.save(entryForm);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Servicio agregado correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al agregar el servicio: " + e.getMessage());
        }

        return "redirect:/moderador/EntradasYSalidas/editar/" + id;
    }


    @ModelAttribute("productosDisponibles")
    public List<Producto> getProductosDisponibles() {
        return productoRepository.findAll();
    }

    @Transactional
    @PostMapping("/moderador/EntradasYSalidas/editar/{id}/agregar-producto")
    public String agregarProductoAEditado(@PathVariable Long id,
                                          @RequestParam("productoId") Long productoId,
                                          RedirectAttributes redirectAttributes) {
        try {
            EntryForm entryForm = entryFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() > 0) {
                producto.setStock(producto.getStock() - 1); // Disminuir el stock
                productoRepository.save(producto);

                if (entryForm.getProductosSeleccionados() == null) {
                    entryForm.setProductosSeleccionados(new ArrayList<>());
                }

                entryForm.getProductosSeleccionados().add(producto);
                entryFormRepository.save(entryForm);

                redirectAttributes.addFlashAttribute("successMessage", "Producto agregado correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Stock insuficiente para el producto seleccionado.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al agregar el producto: " + e.getMessage());
        }

        return "redirect:/moderador/EntradasYSalidas/editar/" + id;
    }


    @PostMapping("/moderador/EntradasYSalidas/editar/{id}")
    @Transactional
    public String guardarEdicion(@PathVariable Long id,
                                 @ModelAttribute("entryForm") EntryForm formData,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Obtener el registro existente
            EntryForm entryForm = entryFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

            // Actualizar los datos del propietario
            entryForm.setNombrePropietario(formData.getNombrePropietario());
            entryForm.setCedulaCiudadania(formData.getCedulaCiudadania());
            entryForm.setCorreo(formData.getCorreo());
            entryForm.setCelular(formData.getCelular());
            entryForm.setQueVaASer(formData.getQueVaASer());

            // Actualizar mascotas
            entryForm.setMascotas(formData.getMascotas());

            // Actualizar autorizaciones
            entryForm.setAutorizacionForm(formData.getAutorizacionForm());

            // Mantener productos seleccionados
            if (formData.getProductosSeleccionados() != null) {
                entryForm.setProductosSeleccionados(formData.getProductosSeleccionados());
            }

            // Guardar cambios
            entryFormRepository.saveAndFlush(entryForm);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Cambios guardados correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al guardar los cambios: " + e.getMessage());
        }

        return "redirect:/moderador/EntradasYSalidas/editar/" + id;
    }

    @PostMapping("/moderador/EntradasYSalidas/editar/{id}/eliminar-producto")
    @Transactional
    public String eliminarProducto(@PathVariable Long id,
                                   @RequestParam Long productoId,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Obtener el registro existente
            EntryForm entryForm = entryFormRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

            if (entryForm.getProductosSeleccionados() != null) {
                // Buscar y eliminar el producto por ID
                boolean removed = entryForm.getProductosSeleccionados()
                        .removeIf(producto -> producto.getId().equals(productoId));

                if (removed) {
                    // Guardar los cambios en la base de datos
                    entryFormRepository.saveAndFlush(entryForm);
                    redirectAttributes.addFlashAttribute("successMessage", "Producto eliminado correctamente.");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Producto no encontrado en la lista.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "No hay productos seleccionados para eliminar.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el producto: " + e.getMessage());
        }

        return "redirect:/moderador/EntradasYSalidas/editar/" + id;
    }
@GetMapping("/moderador/EntradasYSalidas/factura/{id}")
public String mostrarFactura(@PathVariable Long id, Model model) {
    try {
        EntryForm entryForm = entryFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        // Calcular el total
        double total = 0;
        if (entryForm.getServicioSeleccionado() != null) {
            total += entryForm.getServicioSeleccionado().getPrecio();
        }
        if (entryForm.getProductosSeleccionados() != null) {
            total += entryForm.getProductosSeleccionados().stream()
                    .mapToDouble(Producto::getPrecio)
                    .sum();
        }

        model.addAttribute("entryForm", entryForm);
        model.addAttribute("total", total);
        model.addAttribute("metodoPago", entryForm.getMetodoPago());

        return "moderador/factura";
    } catch (Exception e) {
        return "redirect:/moderador/EntradasYSalidas";
    }
}

}

