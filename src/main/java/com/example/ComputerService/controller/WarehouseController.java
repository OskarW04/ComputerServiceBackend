package com.example.ComputerService.controller;

import com.example.ComputerService.dto.request.PartRequest;
import com.example.ComputerService.dto.request.SupplyOrderRequest;
import com.example.ComputerService.model.PartOrder;
import com.example.ComputerService.model.SparePart;
import com.example.ComputerService.repository.SparePartRepository;
import com.example.ComputerService.service.WarehouseService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @GetMapping("/getAllParts")
    @PreAuthorize("hasAnyRole('WAREHOUSE', 'TECHNICIAN', 'MANAGER')")
    public ResponseEntity<List<SparePart>> getAllParts() {
        return ResponseEntity.ok(warehouseService.getAllParts());
    }

    @PostMapping("/addPart")
    @PreAuthorize("hasRole('WAREHOUSE')")
    public ResponseEntity<SparePart> addNewPart(@RequestBody SparePart part) {
        return ResponseEntity.ok(warehouseService.addNewSparePart(part));
    }

    // Deliveries
    @GetMapping("/order/getAllOrders")
    @PreAuthorize("hasRole('WAREHOUSE')")
    public ResponseEntity<List<PartOrder>> getAllSupplyOrders() {
        return ResponseEntity.ok(warehouseService.getAllSupplyOrders());
    }

    @PostMapping("/order/createOrder")
    @PreAuthorize("hasRole('WAREHOUSE')")
    public ResponseEntity<PartOrder> createSupplyOrder(@RequestBody SupplyOrderRequest request) {
        return ResponseEntity.ok(warehouseService.createSupplyOrder(request));
    }

    @PostMapping("/order/{id}/receive")
    @PreAuthorize("hasRole('WAREHOUSE')")
    public ResponseEntity<String> receiveSupply(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.receiveSupply(id));
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('WAREHOUSE')")
    public ResponseEntity<String> withdrawPart(@RequestBody PartRequest request) {
        return ResponseEntity.ok(warehouseService.withdrawPart(request));
    }
}
