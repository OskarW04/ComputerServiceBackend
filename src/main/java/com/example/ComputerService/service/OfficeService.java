package com.example.ComputerService.service;

import com.example.ComputerService.model.CostEstimate;
import com.example.ComputerService.model.RepairOrder;
import com.example.ComputerService.model.enums.RepairOrderStatus;
import com.example.ComputerService.repository.CostEstRepository;
import com.example.ComputerService.repository.RepairOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OfficeService {
    private final CostEstRepository costEstRepository;
    private final RepairOrderRepository orderRepository;

    @Transactional
    public String acceptCostEstimateForClient(Long orderId){
        RepairOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order doesn't exist"));
        CostEstimate est = costEstRepository.findByRepairOrder(order)
                .orElseThrow(() -> new RuntimeException("Can't find cost estimate assigned to that order"));
        if(order.getStatus() != RepairOrderStatus.WAITING_FOR_ACCEPTANCE){
            throw new RuntimeException("This order is not waiting for acceptance");
        }

        est.setApproved(Boolean.TRUE);
        order.setStatus(RepairOrderStatus.IN_PROGRESS);
        orderRepository.save(order);
        costEstRepository.save(est);
        return "Accepted repair for order number " + order.getOrderNumber();
    }

    @Transactional
    public String rejectCostEstimateForClient(Long orderId){
        RepairOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order doesn't exist"));
        CostEstimate est = costEstRepository.findByRepairOrder(order)
                .orElseThrow(() -> new RuntimeException("Can't find cost estimate assigned to that order"));
        if(order.getStatus() != RepairOrderStatus.WAITING_FOR_ACCEPTANCE){
            throw new RuntimeException("This order is not waiting for acceptance");
        }
        est.setApproved(Boolean.FALSE);
        order.setStatus(RepairOrderStatus.CANCELLED);
        est.setLabourCost(new BigDecimal("50.00"));
        est.setPartsCost(BigDecimal.ZERO);
        orderRepository.save(order);
        costEstRepository.save(est);
        return "Rejected repair for order number " + order.getOrderNumber();
    }
}
