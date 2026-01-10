package com.example.ComputerService.service;

import com.example.ComputerService.model.CostEstimate;
import com.example.ComputerService.model.PartUsage;
import com.example.ComputerService.model.RepairOrder;
import com.example.ComputerService.model.SparePart;
import com.example.ComputerService.model.enums.RepairOrderStatus;
import com.example.ComputerService.repository.CostEstRepository;
import com.example.ComputerService.repository.PartUsageRepository;
import com.example.ComputerService.repository.RepairOrderRepository;
import com.example.ComputerService.repository.SparePartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfficeService {
    private final CostEstRepository costEstRepository;
    private final RepairOrderRepository orderRepository;
    private final PartUsageRepository partUsageRepository;
    private final SparePartRepository sparePartRepository;

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
        List<PartUsage> plannedParts = partUsageRepository.findByRepairOrder(order);
        boolean allPartsAvailable = true;
        for (PartUsage usage : plannedParts) {
            SparePart part = usage.getSparePart();
            if (part.getStockQuantity() < usage.getQuantity()) {
                allPartsAvailable = false;
                break;
            }
        }
        if (allPartsAvailable) {
            for (PartUsage usage : plannedParts) {
                SparePart part = usage.getSparePart();
                part.setStockQuantity(part.getStockQuantity() - usage.getQuantity());
                sparePartRepository.save(part);
            }
            order.setStatus(RepairOrderStatus.IN_PROGRESS);
        } else {
            order.setStatus(RepairOrderStatus.WAITING_FOR_PARTS);
        }
        orderRepository.save(order);
        costEstRepository.save(est);
        if(allPartsAvailable){
            return "Accepted repair for order number " + order.getOrderNumber() + ", all parts available";
        }else{
            return "Accepted repair for order number " + order.getOrderNumber() + ", but parts have to be ordered";
        }
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

        List<PartUsage> usedParts = partUsageRepository.findByRepairOrder(order);

        for (PartUsage usage : usedParts) {
            SparePart part = usage.getSparePart();
            part.setStockQuantity(part.getStockQuantity() + usage.getQuantity());
            sparePartRepository.save(part);
            partUsageRepository.delete(usage);
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
