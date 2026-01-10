package com.example.ComputerService.service;

import com.example.ComputerService.dto.request.ClientRequest;
import com.example.ComputerService.dto.response.ClientResponse;
import com.example.ComputerService.mapper.ClientMapper;
import com.example.ComputerService.model.*;
import com.example.ComputerService.model.enums.RepairOrderStatus;
import com.example.ComputerService.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final CostEstRepository costEstRepository;
    private final RepairOrderRepository orderRepository;
    private final PartUsageRepository partUsageRepository;
    private final SparePartRepository sparePartRepository;

    @Transactional
    public ClientResponse createClient(ClientRequest request){
        if(clientRepository.findByPhone(request.getPhone()).isPresent()){
            throw new RuntimeException("Client with that phone number already exists");
        }

        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPhone(request.getPhone());
        client.setEmail(request.getEmail());

        client.setPin(null);

        Client newClient = clientRepository.save(client);
        return clientMapper.mapToResponse(newClient);
    }

    @Transactional
    public Client verifyPIN(String phone, String pin){
        Client client = clientRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Client with this phone number doesnt exist"));

        if (client.getPin() == null) {
            throw new RuntimeException("Generate new PIN");
        }

        if (!client.getPin().equals(pin)) {
            throw new RuntimeException("Provided wrong PIN");
        }

        client.setPin(null);
        clientRepository.save(client);

        return client;
    }

    public List<ClientResponse> getAllClients(){
         return clientRepository.findAll().stream()
                 .map(clientMapper::mapToResponse)
                 .collect(Collectors.toList());
    }

    public ClientResponse getClientByPhone(String phone){
        Client client = clientRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Client does not exist"));
        return clientMapper.mapToResponse(client);
    }

    @Transactional
    public String acceptCostEstimate(Long orderId, String phone){
        RepairOrder order =  orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order doesn't exist"));
        Client client = order.getClient();
        CostEstimate est = costEstRepository.findByRepairOrder(order)
                .orElseThrow(() -> new RuntimeException("This order don't have cost estimate assigned"));
        if(!client.getPhone().equals(phone)){
            throw new RuntimeException("This cost estimate doesn't belong to this client");
        }
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
    public String rejectCostEstimate(Long orderId, String phone){
        RepairOrder order =  orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order doesn't exist"));
        Client client = order.getClient();
        CostEstimate est = costEstRepository.findByRepairOrder(order)
                .orElseThrow(() -> new RuntimeException("This order don't have cost estimate assigned"));
        if(!client.getPhone().equals(phone)){
            throw new RuntimeException("This cost estimate doesn't belong to this client");
        }
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
