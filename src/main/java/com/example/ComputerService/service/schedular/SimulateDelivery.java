package com.example.ComputerService.service.schedular;

import com.example.ComputerService.model.PartOrder;
import com.example.ComputerService.model.enums.PartOrderStatus;
import com.example.ComputerService.repository.PartOrderRepository;
import com.example.ComputerService.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulateDelivery {

    private final PartOrderRepository partOrderRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void simulateDeliveryFlow() {
        System.out.println("Beginning delivery simulation");
        List<PartOrder> activeOrders = partOrderRepository.findAll();

        for (PartOrder order : activeOrders) {
            if (order.getStatus() == PartOrderStatus.ORDERED) {
                System.out.println("Order ID: " + order.getId() + " Is now IN_DELIVERY.");
                order.setStatus(PartOrderStatus.IN_DELIVERY);
                partOrderRepository.save(order);
            }

            else if (order.getStatus() == PartOrderStatus.IN_DELIVERY) {
                System.out.println("Order ID: " + order.getId() + " was delivered");
            }
        }
    }
}
