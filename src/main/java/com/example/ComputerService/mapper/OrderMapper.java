package com.example.ComputerService.mapper;

import com.example.ComputerService.dto.response.CostEstimateResponse;
import com.example.ComputerService.dto.response.OrderResponse;
import com.example.ComputerService.model.CostEstimate;
import com.example.ComputerService.model.RepairOrder;
import com.example.ComputerService.repository.CostEstRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final CostEstRepository costEstRepository;
    private final CostEstimateMapper costEstimateMapper;

    public OrderResponse mapToResponse(RepairOrder order){
        CostEstimate estimate = costEstRepository.findByRepairOrder(order)
                .orElse(null);
        CostEstimateResponse estResponse = null;
        if (estimate != null) {
            estResponse = costEstimateMapper.mapToResponse(estimate);
        }

        return mapToResponse(order, estResponse);
    }

    public OrderResponse mapToResponse(RepairOrder order, CostEstimateResponse est){
        // technician may be null if order is new
        String techName = null;
        if(order.getAssignedTechnician() != null){
            techName = order.getAssignedTechnician().getFirstName() +
                    " " + order.getAssignedTechnician().getLastName();
        }

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCreatedAt(),
                order.getStartDate(),
                order.getEndDate(),
                order.getDeviceDescription(),
                order.getProblemDescription(),
                order.getStatus().name(),
                order.getClient().getId(),
                order.getClient().getFirstName() + " " + order.getClient().getLastName(),
                order.getClient().getPhone(),
                techName,
                order.getManagerNotes(),
                est
        );

    }
}
