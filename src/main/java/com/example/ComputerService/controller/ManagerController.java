package com.example.ComputerService.controller;

import com.example.ComputerService.dto.response.OrderResponse;
import com.example.ComputerService.model.ServiceAction;
import com.example.ComputerService.repository.RepairOrderRepository;
import com.example.ComputerService.service.OrderService;
import com.example.ComputerService.service.ServiceActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {
    private final OrderService orderService;
    private final ServiceActionService actionService;
    @PutMapping("/{orderId}/assign/{technicianId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<OrderResponse> assignTechnician(@PathVariable Long orderId, @PathVariable Long technicianId) {
        return ResponseEntity.ok(orderService.assignTechnician(orderId, technicianId));
    }

    @PostMapping("/services/add")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ServiceAction> addService(@RequestBody ServiceAction serviceAction) {
        return ResponseEntity.ok(actionService.addService(serviceAction));
    }
    @PutMapping("services/edit/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ServiceAction> updateService(@PathVariable Long id, @RequestBody ServiceAction updated) {
        return ResponseEntity.ok(actionService.updateService(id, updated));
    }

    @DeleteMapping("services/delete/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteService(@PathVariable Long id){
        return ResponseEntity.ok(actionService.deleteService(id));
    }

}
