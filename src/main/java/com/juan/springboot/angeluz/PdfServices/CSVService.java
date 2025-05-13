package com.juan.springboot.angeluz.PdfServices;

import com.juan.springboot.angeluz.forms.EntryForm;
import com.juan.springboot.angeluz.shop.Producto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CSVService {

    public ByteArrayInputStream generarCSV(List<EntryForm> entradas) {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader(
                "Fecha",
                "Cliente",
                "Servicio",
                "Productos",
                "Total",
                "Pago"
        ).withDelimiter(';');

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(osw, format)) {

            byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            out.write(bom);

            for (EntryForm entrada : entradas) {
                String fechaInicio = entrada.getFechaInicio() != null ? entrada.getFechaInicio().toString() : "N/A";
                String cliente = entrada.getNombrePropietario() != null ? entrada.getNombrePropietario() : "N/A";
                String servicio = entrada.getQueVaASer() != null ? entrada.getQueVaASer() : "N/A";
                String productos = getProductosString(entrada.getProductosSeleccionados());
                double total = entrada.getValorTotal() != null ? entrada.getValorTotal() : 0.0;
                String pago = entrada.getEstadoPago() != null ? entrada.getEstadoPago() : "Pendiente";

                printer.printRecord(
                        fechaInicio,
                        cliente,
                        servicio,
                        productos,
                        String.format("%.2f", total),
                        pago
                );
            }

            printer.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo CSV: ", e);
        }
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