package com.example.ComputerService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private LocalDateTime createdAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String deviceDescription;
    private String problemDescription;
    private String status;

    private Long clientId;
    private String clientName;
    private String clientPhone;
    private String technicianName;
    private String managerNotes;
    private CostEstimateResponse costEstimateResponse;
}
