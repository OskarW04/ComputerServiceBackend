package com.example.ComputerService.dto.request;

import lombok.Data;

@Data
public class PartRequest {
    private Long sparePartId;
    private Integer quantity;
}
