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

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLUE);
            Paragraph title = new Paragraph("Reporte de Entradas", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Modificar la tabla para incluir la columna de pago
            PdfPTable table = new PdfPTable(6); // Ahora 6 columnas
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 3, 3, 3, 2, 2}); // Ajustar los anchos
            table.setSpacingBefore(10);

            addTableHeader(table);
            addTableData(table, entradas);

            document.add(table);

            double sumaTotal = entradas.stream()
                    .mapToDouble(e -> e.getValorTotal() != null ? e.getValorTotal() : 0.0)
                    .sum();

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
        // Agregar "Pago" a los encabezados
        String[] headers = {"Fecha", "Cliente", "Servicio", "Productos", "Total", "Pago"};
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private void addTableData(PdfPTable table, List<EntryForm> entradas) {
    Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

    for (EntryForm entrada : entradas) {
        // Fecha
        addCell(table, entrada.getFechaInicio() != null ? entrada.getFechaInicio().toString() : "N/A", dataFont);

        // Cliente
        addCell(table, entrada.getNombrePropietario() != null ? entrada.getNombrePropietario() : "N/A", dataFont);

        // Servicio
        addCell(table, entrada.getQueVaASer() != null ? entrada.getQueVaASer() : "N/A", dataFont);

        // Productos
        addCell(table, getProductosString(entrada.getProductosSeleccionados()), dataFont);

        // Total
        PdfPCell totalCell = new PdfPCell(new Phrase(
                "$" + (entrada.getValorTotal() != null ? entrada.getValorTotal() : "0.00"), dataFont));
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalCell.setPadding(5);
        table.addCell(totalCell);

        // Estado de Pago
        String estadoPago = entrada.getEstadoPago() != null ? entrada.getEstadoPago() : "Pendiente";
        BaseColor colorPago = "Pagado".equals(estadoPago) ? BaseColor.GREEN : BaseColor.RED;
        Font pagoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, colorPago);
        PdfPCell pagoCell = new PdfPCell(new Phrase(estadoPago, pagoFont));
        pagoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pagoCell.setPadding(5);
        table.addCell(pagoCell);
    }
}
    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private String getProductosString(List<Producto> productos) {
        if (productos == null || productos.isEmpty()) {
            return "Sin productos";
        }
        return productos.stream()
                .map(Producto::getNombre)
                .collect(Collectors.joining(", "));
    }
}