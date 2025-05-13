package com.juan.springboot.angeluz.PdfServices;

import com.juan.springboot.angeluz.PdfServices.CSVService; // Importa el servicio CSV
import com.juan.springboot.angeluz.forms.EntryForm;
import com.juan.springboot.angeluz.forms.EntryFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/admin/reportes")
public class ReporteController {

    @Autowired
    private PDFService pdfService;

    @Autowired
    private EntryFormService entryFormService;

    @Autowired
    private CSVService csvService; // Inyecta el servicio CSV

    @GetMapping("/generar")
    public ResponseEntity<InputStreamResource> generarReporte(
            @RequestParam("tipo") String tipo,
            @RequestParam("formato") String formato,
            @RequestParam(value = "mes", required = false) Integer mes) {

        // Obtener las entradas (esta parte es común para ambos formatos)
        List<EntryForm> entradas = entryFormService.getAllEntries();

        // Filtrar por mes (también común)
        if (mes != null) {
            entradas = entradas.stream()
                    .filter(e -> e.getFechaInicio() != null && e.getFechaInicio().getMonthValue() == mes)
                    .toList();
        }

        if ("reporte".equalsIgnoreCase(tipo)) {
            if ("pdf".equalsIgnoreCase(formato)) {
                // Generar el PDF (tu código original)
                ByteArrayInputStream pdfStream = pdfService.generarReportePDF(entradas);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "inline; filename=reporte.pdf");
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(pdfStream));
            } else if ("csv".equalsIgnoreCase(formato)) {
                // Generar el CSV (la nueva lógica)
                ByteArrayInputStream csvStream = csvService.generarCSV(entradas);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachment; filename=reporte.csv");
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.TEXT_PLAIN) // O MediaType.TEXT_CSV
                        .body(new InputStreamResource(csvStream));
            } else {
                return ResponseEntity.badRequest().body(new InputStreamResource(new ByteArrayInputStream("Formato no soportado".getBytes())));
            }
        } else {
            return ResponseEntity.badRequest().body(new InputStreamResource(new ByteArrayInputStream("Tipo de reporte no soportado".getBytes())));
        }
    }
}