package com.example.ComputerService.service;

import com.example.ComputerService.dto.request.PartRequest;
import com.example.ComputerService.dto.request.SupplyOrderRequest;
import com.example.ComputerService.model.PartOrder;
import com.example.ComputerService.model.PartUsage;
import com.example.ComputerService.model.RepairOrder;
import com.example.ComputerService.model.SparePart;
import com.example.ComputerService.model.enums.PartOrderStatus;
import com.example.ComputerService.model.enums.RepairOrderStatus;
import com.example.ComputerService.repository.PartOrderRepository;
import com.example.ComputerService.repository.PartUsageRepository;
import com.example.ComputerService.repository.RepairOrderRepository;
import com.example.ComputerService.repository.SparePartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final SparePartRepository sparePartRepository;
    private final PartOrderRepository partOrderRepository;
    private final RepairOrderRepository orderRepository;
    private final PartUsageRepository partUsageRepository;

    public SparePart addNewSparePart(SparePart part){
        if (part.getStockQuantity() == null) {
            part.setStockQuantity(0);
        }
        return sparePartRepository.save(part);
    }

    @Transactional
    public PartOrder createSupplyOrder(SupplyOrderRequest request) {
        SparePart part = sparePartRepository.findById(request.getSparePartId())
                .orElseThrow(() -> new RuntimeException("Część nie istnieje w katalogu"));

        PartOrder order = new PartOrder();
        order.setOrderDate(LocalDate.now());
        order.setEstimatedDelivery(LocalDate.now().plusDays(3));
        order.setQuantity(request.getQuantity());
        order.setStatus(PartOrderStatus.ORDERED);
        order.setSparePart(part);

        return partOrderRepository.save(order);
    }

    @Transactional
    public String  receiveSupply(Long partOrderId){
        PartOrder supplyOrder = partOrderRepository.findById(partOrderId)
                .orElseThrow(() -> new RuntimeException("This supply order doesn't exist"));

        if (supplyOrder.getStatus() != PartOrderStatus.DELIVERED && supplyOrder.getStatus() != PartOrderStatus.IN_DELIVERY) {
            throw new RuntimeException("This supply order is not delivered");
        }
        SparePart receiveSparePart = supplyOrder.getSparePart();
        receiveSparePart.setStockQuantity(receiveSparePart.getStockQuantity() + supplyOrder.getQuantity());
        sparePartRepository.save(receiveSparePart);

        List<RepairOrder> waitingOrders = orderRepository.findAllByStatus(RepairOrderStatus.WAITING_FOR_PARTS);
        for (RepairOrder order : waitingOrders) {
            tryActivateOrder(order);
        }
        return "Supply received successfully, Orders statuses, which were waiting for parts, are updated";
    }

    private void tryActivateOrder(RepairOrder order){
        List<PartUsage> requiredParts = partUsageRepository.findByRepairOrder(order);
        boolean canStart = true;

        for (PartUsage usage : requiredParts) {
            SparePart part = usage.getSparePart();
            if (part.getStockQuantity() < usage.getQuantity()) {
                canStart = false;
                break;
            }
        }

        if (canStart) {
            for (PartUsage usage : requiredParts) {
                SparePart part = usage.getSparePart();
                part.setStockQuantity(part.getStockQuantity() - usage.getQuantity());
                sparePartRepository.save(part);
            }

            order.setStatus(RepairOrderStatus.IN_PROGRESS);
            orderRepository.save(order);
        }
    }
    public List<PartOrder> getAllSupplyOrders() {
        return partOrderRepository.findAll();
    }

    public List<SparePart> getAllParts(){
        return sparePartRepository.findAll();
    }

    @Transactional
    public String withdrawPart(PartRequest request){
        SparePart part = sparePartRepository.findById(request.getSparePartId())
                .orElseThrow(() -> new RuntimeException("Part doesn't exist"));

        if (part.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("There is not enough in a warehouse: " + part.getName() +
                    " On stock: " + part.getStockQuantity() +
                    ", Requested: " + request.getQuantity());
        }

        part.setStockQuantity(part.getStockQuantity() - request.getQuantity());
        sparePartRepository.save(part);

       return "Successful withdrawn of part: " + part.getName() + "Now in stock - " + part.getStockQuantity();
    }
}
