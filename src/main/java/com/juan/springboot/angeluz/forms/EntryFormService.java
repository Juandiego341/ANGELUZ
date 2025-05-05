package com.juan.springboot.angeluz.forms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class EntryFormService {

    private final EntryFormRepository entryFormRepository;

    @Autowired
    public EntryFormService(EntryFormRepository entryFormRepository) {
        this.entryFormRepository = entryFormRepository;
    }

    @Transactional(readOnly = true)
    public List<EntryForm> getAllEntries() {
        return entryFormRepository.findAll();
    }

    @Transactional(readOnly = true)
    public int contarCitasActivas() {
        LocalDate hoy = LocalDate.now();
        return entryFormRepository.countByFechaFinGreaterThanEqual(hoy);
    }

    @Transactional(readOnly = true)
    public double calcularIngresosTotales() {
        return entryFormRepository.sumValorTotal();
    }

    public byte[] generarReporte(String tipo, String formato) {
        List<EntryForm> entradas = getAllEntries();
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Fecha,Cliente,Servicio,Valor Total\n");

        for (EntryForm entrada : entradas) {
            csv.append(String.format("%d,%s,%s,%s,%.2f\n",
                entrada.getId(),
                entrada.getFechaInicio(),
                entrada.getCorreo(),
                entrada.getQueVaASer(),
                entrada.getValorTotal()));
        }

        return csv.toString().getBytes();
    }
}