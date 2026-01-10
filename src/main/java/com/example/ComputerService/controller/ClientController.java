package com.example.ComputerService.controller;

import com.example.ComputerService.dto.response.OrderResponse;
import com.example.ComputerService.model.Client;
import com.example.ComputerService.service.ClientService;
import com.example.ComputerService.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class ClientController {
    private final OrderService orderService;
    private final ClientService clientService;

    @GetMapping("/getClientOrders")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<OrderResponse>> getClientOrders(Authentication auth){
        String phone = auth.getName();
        return ResponseEntity.ok(orderService.getOrdersForClient(phone));
    }

    @GetMapping("/getOrder/{orderId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<OrderResponse> getClientOrder(@PathVariable Long orderId, Authentication auth){
        String phone = auth.getName();
        return ResponseEntity.ok(orderService.getClientOrder(orderId, phone));
    }

    @PutMapping("/accept/{orderId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<String> acceptCostEstimate(@PathVariable Long orderId, Authentication auth){
        String phone = auth.getName();
        return ResponseEntity.ok(clientService.acceptCostEstimate(orderId, phone));
    }

    @PutMapping("/reject/{orderId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<String> rejectCostEstimate(@PathVariable Long orderId, Authentication auth){
        String phone = auth.getName();
        return ResponseEntity.ok(clientService.rejectCostEstimate(orderId, phone));
    }

}
