package com.example.ComputerService.controller;

import com.example.ComputerService.dto.request.CostEstimateRequest;
import com.example.ComputerService.dto.response.CostEstimateResponse;
import com.example.ComputerService.dto.response.OrderResponse;
import com.example.ComputerService.service.OrderService;
import com.example.ComputerService.service.TechnicianService;
import lombok.RequiredArgsConstructor;

import org.hibernate.query.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tech")
public class TechnicianController {
    private final OrderService orderService;
    private final TechnicianService technicianService;

    @GetMapping("/getAssignedOrders")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<List<OrderResponse>> getAssignedOrders(Authentication auth){
        String email = auth.getName();
        return ResponseEntity.ok(orderService.getOrdersForTechnician(email));
    }

    @PatchMapping("/{orderId}/startDiagnosing")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<String> startDiagnosing(Authentication auth, @PathVariable Long orderId){
        String email = auth.getName();
        technicianService.startDiagnosing(orderId, email);
        return ResponseEntity.ok("Status of order with ID: " + orderId + " successfully changed to DIAGNOSING by "
                + email);
    }

    @PostMapping("/{orderId}/generateCostEst")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<OrderResponse> generateCostEst(@PathVariable Long orderId, @RequestBody CostEstimateRequest request, Authentication auth){
        String email = auth.getName();
        return ResponseEntity.ok(technicianService.generateCostEst(orderId, request, email));
    }

    @PutMapping("/finish/{orderId}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<String> finishOrder(@PathVariable Long orderId, Authentication auth){
        String email = auth.getName();
        return ResponseEntity.ok(technicianService.finishOrder(orderId, email));
    }
}
