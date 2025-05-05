package com.juan.springboot.angeluz.PdfServices;

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

   @GetMapping("/generar")
    public ResponseEntity<InputStreamResource> generarReporte(
            @RequestParam("tipo") String tipo,
            @RequestParam("formato") String formato,
            @RequestParam(value = "mes", required = false) Integer mes) {

        // Obtener las entradas
        List<EntryForm> entradas = entryFormService.getAllEntries();

        // Filtrar por mes si se proporciona
        if (mes != null) {
            entradas = entradas.stream()
                    .filter(e -> e.getFechaInicio() != null && e.getFechaInicio().getMonthValue() == mes)
                    .toList();
        }

        // Generar el PDF
        ByteArrayInputStream pdfStream = pdfService.generarReportePDF(entradas);

        // Configurar la respuesta HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reporte.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}