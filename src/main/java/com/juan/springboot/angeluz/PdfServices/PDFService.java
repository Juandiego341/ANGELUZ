package com.juan.springboot.angeluz.PdfServices;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.juan.springboot.angeluz.forms.EntryForm;
import com.juan.springboot.angeluz.shop.Producto;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PDFService {

    public ByteArrayInputStream generarReportePDF(List<EntryForm> entradas) {
        Document document = new Document(PageSize.A4, 20, 20, 30, 30);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Título del reporte
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLUE);
            Paragraph title = new Paragraph("Reporte de Entradas", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Crear tabla
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 3, 3, 4, 2});
            table.setSpacingBefore(10);

            // Encabezados
            addTableHeader(table);

            // Datos
            addTableData(table, entradas);

            // Agregar tabla al documento
            document.add(table);

            // Calcular suma total
            double sumaTotal = entradas.stream()
                    .mapToDouble(e -> e.getValorTotal() != null ? e.getValorTotal() : 0.0)
                    .sum();

            // Mostrar suma total
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Paragraph totalParagraph = new Paragraph("Suma Total: $" + String.format("%.2f", sumaTotal), totalFont);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            totalParagraph.setSpacingBefore(10);
            document.add(totalParagraph);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableHeader(PdfPTable table) {
        String[] headers = {"Fecha", "Cliente", "Servicio", "Productos", "Total"};
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8); // Espaciado interno
            table.addCell(cell);
        }
    }

    private void addTableData(PdfPTable table, List<EntryForm> entradas) {
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

        for (EntryForm entrada : entradas) {
            // Fecha
            String fechaInicio = (entrada.getFechaInicio() != null)
                    ? entrada.getFechaInicio().toString()
                    : "N/A";
            PdfPCell fechaCell = new PdfPCell(new Phrase(fechaInicio, dataFont));
            fechaCell.setPadding(5);
            fechaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(fechaCell);

            // Cliente
            PdfPCell clienteCell = new PdfPCell(new Phrase(
                    entrada.getNombrePropietario() != null ? entrada.getNombrePropietario() : "N/A", dataFont));
            clienteCell.setPadding(5);
            table.addCell(clienteCell);

            // Servicio
            PdfPCell servicioCell = new PdfPCell(new Phrase(
                    entrada.getQueVaASer() != null ? entrada.getQueVaASer() : "N/A", dataFont));
            servicioCell.setPadding(5);
            table.addCell(servicioCell);

            // Productos
            PdfPCell productosCell = new PdfPCell(new Phrase(
                    getProductosString(entrada.getProductosSeleccionados()), dataFont));
            productosCell.setPadding(5);
            table.addCell(productosCell);

            // Total
            PdfPCell totalCell = new PdfPCell(new Phrase(
                    "$" + (entrada.getValorTotal() != null ? entrada.getValorTotal() : "0.00"), dataFont));
            totalCell.setPadding(5);
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(totalCell);
        }
    }

    private String getProductosString(List<Producto> productos) {
        if (productos == null || productos.isEmpty()) {
            return "Sin productos";
        }
        return productos.stream()
                .map(Producto::getNombre) // Obtiene el nombre de cada producto
                .collect(Collectors.joining(", ")); // Une los nombres con comas
    }
}