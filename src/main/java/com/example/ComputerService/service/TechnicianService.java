package com.example.ComputerService.service;

import com.example.ComputerService.dto.request.CostEstimateRequest;
import com.example.ComputerService.dto.request.PartRequest;
import com.example.ComputerService.dto.response.OrderResponse;
import com.example.ComputerService.dto.response.PartResponse;
import com.example.ComputerService.mapper.OrderMapper;
import com.example.ComputerService.mapper.PartMapper;
import com.example.ComputerService.model.*;
import com.example.ComputerService.model.enums.EmployeeRole;
import com.example.ComputerService.model.enums.RepairOrderStatus;
import com.example.ComputerService.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechnicianService {
    private final EmployeeRepository employeeRepository;
    private final RepairOrderRepository orderRepository;
    private final CostEstRepository costEstRepository;
    private final PartUsageRepository partUsageRepository;
    private final SparePartRepository sparePartRepository;
    private final OrderMapper orderMapper;
    private final PartMapper partMapper;

    @Transactional
    public void startDiagnosing(Long orderId, String technicianEmail){
        Employee e = employeeRepository.findByEmailAndRole(technicianEmail, EmployeeRole.TECHNICIAN)
                .orElseThrow(() -> new RuntimeException("Cant find Technician with that id number"));

        RepairOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order with that id does not exist"));

        if(order.getAssignedTechnician() != e){
            throw new RuntimeException("This order is not assigned to this technician");
        }

        if(order.getStatus() != RepairOrderStatus.NEW && order.getStatus() != RepairOrderStatus.WAITING_FOR_TECHNICIAN){
            throw new RuntimeException("Status of this order cant be changed");
        }
        order.setStartDate(LocalDateTime.now());
        order.setStatus(RepairOrderStatus.DIAGNOSING);
        orderRepository.save(order);
    }

    @Transactional
    public OrderResponse generateCostEst(Long orderId, CostEstimateRequest request, String techEmail){
        RepairOrder o = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order with that id does not exist"));
        if(o.getAssignedTechnician() == null || !o.getAssignedTechnician().getEmail().equals(techEmail)){
            throw new RuntimeException("This order is not assigned to this technician");
        }
        if(o.getStatus() != RepairOrderStatus.DIAGNOSING){
            throw new RuntimeException("This order is not during diagnosing");
        }
        BigDecimal totalPartsCost = BigDecimal.ZERO;
        if(request.getPartRequestList() != null && !request.getPartRequestList().isEmpty()){
            for(PartRequest pReq : request.getPartRequestList()){
                SparePart part = sparePartRepository.findById(pReq.getSparePartId())
                        .orElseThrow(() -> new RuntimeException("Can't find part with id: " + pReq.getSparePartId()));

                PartUsage usage = new PartUsage();
                usage.setRepairOrder(o);
                usage.setSparePart(part);
                usage.setQuantity(pReq.getQuantity());
                usage.setUnitPrice(part.getPrice());

                partUsageRepository.save(usage);

                BigDecimal cost = part.getPrice().multiply(BigDecimal.valueOf(pReq.getQuantity()));
                totalPartsCost = totalPartsCost.add(cost);
            }
        }
        String currNote = o.getManagerNotes() != null ? o.getManagerNotes()+"\n" : "";
        o.setManagerNotes(currNote + "[DIAGNOSIS] " + request.getMessage());
        o.setStatus(RepairOrderStatus.WAITING_FOR_ACCEPTANCE);
        CostEstimate estimate = new CostEstimate();
        estimate.setApproved(null);
        BigDecimal labour = request.getLabourCost();
        estimate.setLabourCost(labour);
        estimate.setPartsCost(totalPartsCost);
        estimate.setCreatedAt(LocalDateTime.now());
        estimate.setRepairOrder(o);
        costEstRepository.save(estimate);
        RepairOrder savedOrder = orderRepository.save(o);
        return orderMapper.mapToResponse(savedOrder);
    }


    public String finishOrder(Long orderId, String techEmail){
        RepairOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order doesn't exist"));
        if(!order.getAssignedTechnician().getEmail().equals(techEmail)){
            throw new RuntimeException("This order is not assigned to this technician");
        }
        order.setStatus(RepairOrderStatus.READY_FOR_PICKUP);
        order.setEndDate(LocalDateTime.now());
        RepairOrder savedOrder = orderRepository.save(order);
        return "Order with number: "+ savedOrder.getOrderNumber() +" has been set as finished and ready for pickup by client";
    }

    public List<PartResponse> getAllParts(){
        return sparePartRepository.findAll()
                .stream()
                .map(partMapper::mapToResponse)
                .collect(Collectors.toList());
    }



}
