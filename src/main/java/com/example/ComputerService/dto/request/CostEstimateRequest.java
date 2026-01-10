package com.example.ComputerService.dto.request;

import jakarta.servlet.http.Part;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CostEstimateRequest {
    private String message;
    private BigDecimal partsCost;
    private BigDecimal labourCost;
    private List<PartRequest> partRequestList;
}
