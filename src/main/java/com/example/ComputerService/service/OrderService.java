package com.example.ComputerService.service;

import com.example.ComputerService.dto.request.OrderRequest;
import com.example.ComputerService.dto.response.OrderResponse;
import com.example.ComputerService.mapper.OrderMapper;
import com.example.ComputerService.model.Client;
import com.example.ComputerService.model.Employee;
import com.example.ComputerService.model.RepairOrder;
import com.example.ComputerService.model.enums.EmployeeRole;
import com.example.ComputerService.model.enums.RepairOrderStatus;
import com.example.ComputerService.repository.ClientRepository;
import com.example.ComputerService.repository.EmployeeRepository;
import com.example.ComputerService.repository.RepairOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final RepairOrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // find client
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client with provided id does not exist"));
        RepairOrder order = new RepairOrder();

        order.setClient(client);
        order.setDeviceDescription(request.getDeviceDescription());
        order.setProblemDescription(request.getProblemDescription());

        order.setStatus(RepairOrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

        // generate order number as "OR-yyyyMMdd-4_random_nums"
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = String.valueOf((int)(Math.random() * 9000) + 1000);
        order.setOrderNumber("OR-" + datePart + "-" + randomSuffix);

        RepairOrder newOrder = orderRepository.save(order);
        return orderMapper.mapToResponse(newOrder, null);
    }

    @Transactional
    public OrderResponse assignTechnician(Long orderId, Long technicianId) {
        RepairOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Failed to find order with that id"));

        Employee technician = employeeRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Failed to find technician with that id"));

        if (technician.getRole() != EmployeeRole.TECHNICIAN) {
            throw new RuntimeException("This employee is not technician");
        }

        order.setAssignedTechnician(technician);
        order.setStatus(RepairOrderStatus.WAITING_FOR_TECHNICIAN);

        RepairOrder saved = orderRepository.save(order);
        return orderMapper.mapToResponse(saved, null);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllNewOrders() {
        return orderRepository.findAllByStatus(RepairOrderStatus.NEW).stream()
                .map(orderMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersForTechnician(String email) {
        Employee technician = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Technician with this email does not exist: " + email));

        List<RepairOrder> orders = orderRepository.findByAssignedTechnician(technician);

        return orders.stream()
                .map(orderMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersForClient(String phone) {
        Client c = clientRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        List<RepairOrder> orders = orderRepository.findByClient(c);

        return orders.stream()
                .map(orderMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getClientOrder(Long orderId, String phone){
        Client c = clientRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        RepairOrder o = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if(!o.getClient().equals(c)){
            throw new RuntimeException("This order does not belong to this client");
        }

        return orderMapper.mapToResponse(o);
    }


}
