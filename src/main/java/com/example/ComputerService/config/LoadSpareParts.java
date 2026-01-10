package com.example.ComputerService.config;

import com.example.ComputerService.model.SparePart;
import com.example.ComputerService.repository.SparePartRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class LoadSpareParts implements CommandLineRunner {
    private final SparePartRepository sparePartRepository;

    public LoadSpareParts(SparePartRepository sparePartRepository){
        this.sparePartRepository = sparePartRepository;
    }
    @Override
    public void run(String... args){
        if(sparePartRepository.count() == 0){
            List<SparePart> parts = List.of(
                    new SparePart(null, "Intel Core i5-12400F", "CPU", 5, new BigDecimal("800.00")),
                    new SparePart(null, "AMD Ryzen 5 5600", "CPU", 5, new BigDecimal("650.00")),
                    new SparePart(null, "Kingston Fury 16GB", "RAM", 5, new BigDecimal("200.00")),
                    new SparePart(null, "Samsung 970 EVO 1TB", "DISK", 5, new BigDecimal("350.00")),
                    new SparePart(null, "SilentiumPC Vero L3 600W", "PSU", 5, new BigDecimal("250.00"))
            );
            sparePartRepository.saveAll(parts);
            System.out.println("Initialized spare part database");
        }

    }
}
