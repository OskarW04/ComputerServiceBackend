package com.example.ComputerService.dto.request;

import com.example.ComputerService.model.SparePart;
import com.example.ComputerService.model.enums.PartOrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class SupplyOrderRequest {
    private Integer quantity;
    private PartOrderStatus status;
    private Long sparePartId;
}
